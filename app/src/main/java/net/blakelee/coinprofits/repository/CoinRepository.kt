package net.blakelee.coinprofits.repository

import com.google.gson.JsonArray
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.repository.db.CoinDao
import net.blakelee.coinprofits.repository.rest.CoinApi
import net.blakelee.coinprofits.tools.toCoin
import javax.inject.Inject

class CoinRepository @Inject constructor(
        private val db: CoinDao,
        private val api: CoinApi
) {
    private var convert: String = "usd"

    var coins: Flowable<List<Coin>> = db.getCoins()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    fun getCoins(convert: String = "usd"): Observable<MutableList<Coin>> =
        api.getCoins(null, convert)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(this::makeCoins)
                .doOnNext {
                    Observable.fromCallable {
                        db.insertCoins(*it.toTypedArray())
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                }

    fun getCoinById(id: String, convert: String = "usd") =
        api.getCoinById(id, convert)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map{item -> item[0].asJsonObject.toCoin(convert)}
                .filter { it.id != "" }
                .onErrorResumeNext(Observable.empty())

    fun deleteAllCoins() =
            Observable.fromCallable {
                db.deleteAllCoins()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    private fun makeCoins(results: JsonArray): MutableList<Coin> {
        val coins: MutableList<Coin> = mutableListOf()
        if (results.size() != 0) {
            results.forEach {
                val coin = it.asJsonObject.toCoin(convert)
                if (coin.id != "")
                    coins.add(coin)
            }
        }
        return coins
    }
}