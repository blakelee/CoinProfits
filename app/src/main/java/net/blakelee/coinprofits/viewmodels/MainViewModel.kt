package net.blakelee.coinprofits.viewmodels

import android.app.Application
import android.arch.lifecycle.*
import android.content.SharedPreferences
import android.databinding.ObservableField
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.yarolegovich.lovelydialog.LovelyProgressObservable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import net.blakelee.coinprofits.App
import net.blakelee.coinprofits.databases.AppDatabase
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.service.model.ticker
import net.blakelee.coinprofits.service.repository.CoinMarketCapApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val IMAGE_URL = "https://files.coinmarketcap.com/static/img/coins/64x64/"

    @Inject lateinit var db: AppDatabase
    @Inject lateinit var prefs: SharedPreferences
    @Inject lateinit var api: CoinMarketCapApi
    @Inject lateinit var picasso: Picasso

    private var coin: List<Coin>? = null
    var holdings: MediatorLiveData<List<Holdings>> = MediatorLiveData()

    //View databinding
    var is_refreshing = ObservableField<Boolean>(false)
    val last_updated by lazy { ObservableField<String>(prefs.getString("last_updated", getTime())) }
    var holdings_size = ObservableField<Boolean>(true)

    //Preferences
    var first = MediatorLiveData<Boolean>()
    var refresh_tickers = MediatorLiveData<Boolean>()
    var holdings_order: Boolean

    init {
        App.component.inject(this)
        holdings_order = prefs.getBoolean("holdings_order", false)
        getHoldings()
    }

    fun checkPreferences() {
        //Auto refresh holdings on startup if true
        if (prefs.getBoolean("auto_refresh", false) &&
                !prefs.getBoolean("first", true))
            refreshHoldings()

        //Get ALL tickers
        if (prefs.getBoolean("first", true)) {
            deleteAllCoins()
            first.postValue(true)
            prefs.edit().putBoolean("first", false).apply()
            prefs.edit().putBoolean("refresh_tickers", false).apply()
            prefs.edit().putString("last_updated", getTime()).apply()
        }

        //Get new tickers
        if (prefs.getBoolean("refresh_tickers", false) &&
                !prefs.getBoolean("first", true)) {
            refresh_tickers.postValue(true)
            prefs.edit().putBoolean("refresh_tickers", false).apply()
        }

        //Check holdings order
        if (prefs.getBoolean("holdings_order", false) != holdings_order) {
            holdings_order = holdings_order.xor(true)
            getHoldings()
        }
    }

    //Get tickers and store them in the database
    fun getTickers(setTotal: (Int) -> Unit, onFailure: (String) -> Unit, lovelyProgressObservable: LovelyProgressObservable) {
        val tickers: Call<MutableList<ticker>> = api.getTicker()

        tickers.enqueue(object: Callback<MutableList<ticker>> {
            override fun onResponse(call: Call<MutableList<ticker>>?, response: Response<MutableList<ticker>>?) {

                if (response?.body() == null)
                    return

                //No new coins to get
                if (response.body()?.size == db.coinModel().getCoinCount()) {
                    setTotal(0)
                    return
                }

                response.body()?.sortBy { it.id }

                val total = response.body()!!.size - getCoinCount()

                setTotal(total)

                if (coin == null)
                    coin = db.coinModel().getCoins()

                val job = async(CommonPool) {
                    //Go through each ticker and make sure it's in the db
                    response.body()?.forEachIndexed { index, ticker ->
                        val index: Int = coin!!.binarySearchBy(ticker.id) { it.id }

                        //Insert coin into db and picture into local storage
                        if (index < 0) {

                            val mCoin = Coin()
                            mCoin.id = ticker.id
                            mCoin.name = ticker.name
                            mCoin.symbol = ticker.symbol
                            mCoin.price = ticker.priceUsd?.toDouble()
                            mCoin.price_btc = ticker.priceBtc?.toDouble()
                            mCoin.percent_change_24h = ticker.get24hVolumeUsd()?.toDouble()
                            mCoin.market_cap = ticker.marketCapUsd?.toDouble()
                            mCoin.total_supply = ticker.totalSupply?.toDouble()
                            mCoin.percent_change_1h = ticker.percentChange1h?.toDouble()
                            mCoin.percent_change_24h = ticker.percentChange24h?.toDouble()
                            mCoin.percent_change_7d = ticker.percentChange7d?.toDouble()

                            db.coinModel().insertCoin(mCoin)
                        }

                        async(UI) {
                            lovelyProgressObservable.progress += 1
                        }
                    }
                }

                async(UI) {
                    job.await()
                    getHoldings()
                }
            }

            override fun onFailure(call: Call<MutableList<ticker>>?, t: Throwable?) {
                Log.i("RETROFIT", "Failed to get tickers")
                onFailure(t?.cause.toString())
            }
        })
    }

    fun deleteAllCoins() = db.coinModel().deleteAllCoins()

    private fun getCoinCount() = db.coinModel().getCoinCount()

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
        val holdings: LiveData<List<Holdings>>
        if (holdings_order)
            holdings = db.holdingsModel().getHoldingsOrderId()
        else
            holdings = db.holdingsModel().getHoldings()

        this.holdings.addSource(holdings, {
                this.holdings.removeSource(holdings)
                this.holdings.value = it
                if (it == null || it.isEmpty())
                    holdings_size.set(false)
                else
                    holdings_size.set(true)
        })
    }

    fun getHoldingsById(id: String): Holdings? = db.holdingsModel().getHoldingsById(id)

    fun deleteHoldings(holdings: Holdings) {
        db.holdingsModel().deleteHoldings(holdings)
        getHoldings()
    }

    fun refreshHoldings() {
        is_refreshing.set(true)

        db.beginTransaction()
        try {
            holdings.value?.forEach {
                val _ticker: Call<List<ticker>> = api.getCoin(it.id)

                _ticker.enqueue(object : Callback<List<ticker>> {
                    override fun onFailure(call: Call<List<ticker>>?, t: Throwable?) {
                        Toast.makeText(getApplication(), "Couldn't refresh data. Perhaps website or network connection is down", Toast.LENGTH_SHORT).show()
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
        }

        val time = getTime()
        prefs.edit().putString("last_updated", time).apply()
        last_updated.set(time)
        is_refreshing.set(false)
    }

    fun getEth(): Double {
        val _ticker: Call<List<ticker>> = api.getCoin("eth")
        var value = 0.0

        _ticker.enqueue(object : Callback<List<ticker>> {
            override fun onResponse(call: Call<List<ticker>>?, response: Response<List<ticker>>?) {
                if (response?.body() == null)
                    return

                value = response.body()!![0].priceUsd.toDouble()
            }

            //Don't do anything. Leave eth the same
            override fun onFailure(call: Call<List<ticker>>?, t: Throwable?) {}
        })

        return value
    }

    fun getTime(): String = SimpleDateFormat("h:mma", Locale.getDefault()).format(Date())
}