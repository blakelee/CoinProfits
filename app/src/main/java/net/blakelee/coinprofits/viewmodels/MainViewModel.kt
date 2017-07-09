package net.blakelee.coinprofits.viewmodels

import android.app.Application
import android.arch.lifecycle.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import net.blakelee.coinprofits.databases.AppDatabase
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.rest.RestClient
import net.blakelee.coinprofits.rest.model.ticker
import net.blakelee.coinprofits.rest.service.CoinApi
import net.blakelee.coinprofits.tools.toByteArray
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val IMAGE_URL = "https://files.coinmarketcap.com/static/img/coins/64x64/"
    private val db = AppDatabase.createPersistentDatabase(application)
    private var coin: List<Coin>? = null
    private var targets: MutableList<Target> = mutableListOf()
    var holdings: MediatorLiveData<List<Holdings>> = MediatorLiveData()

    init { getHoldings() }

    //Get tickers and store them in the database
    fun refreshTickers(setTotal: (Int) -> Unit, setCompleted: () -> Unit, onFailure: (String) -> Unit) {
        val api: CoinApi = RestClient().getService()
        val tickers: Call<MutableList<ticker>> = api.getTicker()

        tickers.enqueue(object: Callback<MutableList<ticker>> {
            override fun onResponse(call: Call<MutableList<ticker>>?, response: Response<MutableList<ticker>>?) {

                if (response?.body() == null)
                    return

                response.body()?.sortBy { it.id }

                val total = response.body()!!.size

                setTotal(total)

                if (coin == null)
                    coin = db.coinModel().getCoins()

                val context = getApplication<Application>().applicationContext
                val client = OkHttpClient()
                val picasso = Picasso.Builder(context)
                        .downloader(OkHttp3Downloader(client))
                        .build()

                //Go through each ticker and make sure it's in the db
                response.body()?.forEach {
                    val item = it
                    val index: Int = coin!!.binarySearchBy(item.id) { it.id }

                    //Insert coin into db and picture into local storage
                    if (index < 0) {
                        val url = IMAGE_URL + it.id + ".png"

                        val mCoin = Coin()
                        mCoin.id = it.id
                        mCoin.name = it.name
                        mCoin.symbol = it.symbol
                        mCoin.price = it.priceUsd?.toDouble()
                        mCoin.price_btc = it.priceBtc?.toDouble()
                        mCoin.percent_change_24h = it.get24hVolumeUsd()?.toDouble()
                        mCoin.market_cap = it.marketCapUsd?.toDouble()
                        mCoin.total_supply = it.totalSupply?.toDouble()
                        mCoin.percent_change_1h = it.percentChange1h?.toDouble()
                        mCoin.percent_change_24h = it.percentChange24h?.toDouble()
                        mCoin.percent_change_7d = it.percentChange7d?.toDouble()

                        val target = object : Target {
                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                Log.i("PICASSO", "Preparing to get image")
                            }

                            override fun onBitmapFailed(errorDrawable: Drawable?) {
                                Log.i("PICASSO", "Failed to get image")
                                db.coinModel().insertCoin(mCoin)
                                setCompleted()
                            }

                            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                                Log.i("PICASSO", "Successfully got image")
                                mCoin.image = bitmap.toByteArray()
                                db.coinModel().insertCoin(mCoin)
                                setCompleted()
                            }
                        }

                        targets.add(target)
                        val size = targets.size
                        picasso.load(url)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(targets[size - 1])
                    }
                }

                //Remove items that are in the db but not the ticker
                coin!!.forEach {
                    val item = it
                    val index: Int? = response.body()?.binarySearchBy(item.id) { it.id }

                    index?.let {
                        if (index < 0)
                            picasso.invalidate(item.id)
                    }
                }

            }
            override fun onFailure(call: Call<MutableList<ticker>>?, t: Throwable?) {
                Log.i("RETROFIT", "Failed to get tickers")
                onFailure(t?.cause.toString())
            }
        })
    }

    fun getSearchItems(): List<Coin> {
        if (coin == null || coin!!.isEmpty())
            coin = db.coinModel().getCoins()

        return coin!!
    }

    fun insertHoldings(holdings: Holdings) {
        db.holdingsModel().insertHoldings(holdings)
        getHoldings()
    }

    fun updateHoldings(holdings: Holdings) {
        db.holdingsModel().updateHoldings(holdings)
        getHoldings()
    }

    fun getHoldings() {
        val holdings: LiveData<List<Holdings>> = db.holdingsModel().getHoldings()
        this.holdings.addSource(holdings, {
                this.holdings.removeSource(holdings)
                this.holdings.value = it
        })
    }

    fun getHoldingsById(id: String): Holdings? = db.holdingsModel().getHoldingsById(id)

    fun deleteHoldings(holdings: Holdings) {
        db.holdingsModel().deleteHoldings(holdings)
        getHoldings()
    }

    fun refreshHoldings(onFailure: () -> Unit, onSuccess: () -> Unit) {
        val api: CoinApi = RestClient().getService()

        db.beginTransaction()
        try {
            holdings.value!!.forEach {
                val _ticker: Call<List<ticker>> = api.getCoin(it.id)

                _ticker.enqueue(object : Callback<List<ticker>> {
                    override fun onFailure(call: Call<List<ticker>>?, t: Throwable?) {
                        onFailure()
                    }

                    override fun onResponse(call: Call<List<ticker>>?, response: Response<List<ticker>>?) {
                        if (response?.body() == null)
                            return

                        val result = response.body()!![0]
                        val coin = db.coinModel().getCoinById(it.id)

                        coin.price_btc = result.priceBtc?.toDouble()
                        coin.price = result.priceUsd?.toDouble()
                        coin.volume_24h = result.get24hVolumeUsd()?.toDouble()
                        coin.market_cap = result.marketCapUsd?.toDouble()
                        coin.total_supply = result.totalSupply?.toDouble()
                        coin.percent_change_1h = result.percentChange1h?.toDouble()
                        coin.percent_change_24h = result.percentChange24h?.toDouble()
                        coin.percent_change_7d = result.percentChange7d?.toDouble()

                        db.coinModel().updateCoin(coin)
                        updateHoldings(it)
                    }
                })
            }
        }
        finally {
            db.endTransaction()
            onSuccess()
        }
    }
}