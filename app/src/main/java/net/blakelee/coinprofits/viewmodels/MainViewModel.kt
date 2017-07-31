package net.blakelee.coinprofits.viewmodels

import android.arch.lifecycle.*
import android.content.SharedPreferences
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.databases.AppDatabase
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.service.repository.CoinApi
import net.blakelee.coinprofits.tools.makeCoins
import net.blakelee.coinprofits.tools.toCoin
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainViewModel
@Inject constructor (
        private val db: AppDatabase,
        private val prefs: SharedPreferences,
        private val api: CoinApi
) : ViewModel() {

    private var coin: List<Coin>? = null
    var holdings: MediatorLiveData<List<Holdings>> = MediatorLiveData()

    //View databinding
    //var is_refreshing = ObservableField<Boolean>(false)
    val last_updated by lazy { ObservableField<String>(prefs.getString("last_updated", getTime())) }
    var holdings_size = ObservableField<Boolean>(true)

    //Preferences
    var first = MediatorLiveData<Boolean>()
    var refresh_tickers = MediatorLiveData<Boolean>()
    var holdings_order: Boolean
    var is_refreshing = MediatorLiveData<Boolean>()

    init {
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
    fun insertCoins(onFinished: (String?) -> Unit) {

        val convert = prefs.getString("currency", "usd")

        api.getCoins(null, convert)
                .map{ makeCoins(it, convert) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    it.forEach { db.coinModel().insertCoin(it) }
                    onFinished(null)
                }, {
                    onFinished("Failed to get tickers: ${it.message.orEmpty()}")
                })
    }

    fun deleteAllCoins() = db.coinModel().deleteAllCoins()

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

        is_refreshing.postValue(true)

        val convert = prefs.getString("currency", "usd")

        db.beginTransaction()
        holdings.value?.forEach {
            insertCoinById(it.id, convert)
        }
        db.endTransaction()

        getHoldings()

        val time = getTime()
        prefs.edit().putString("last_updated", time).apply()
        last_updated.set(time)
        is_refreshing.postValue(false)
    }

    fun insertCoinById(id: String, convert: String?) {
        api.getCoinById(id, convert)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    val coin = it[0].asJsonObject.toCoin(convert)
                    coin?.let { db.coinModel().updateCoin(it) }
                }
    }

    fun getTime(): String = SimpleDateFormat("h:mma", Locale.getDefault()).format(Date())
}