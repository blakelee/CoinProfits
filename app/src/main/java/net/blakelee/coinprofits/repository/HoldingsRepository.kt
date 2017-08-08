package net.blakelee.coinprofits.repository

import com.google.gson.JsonArray
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.repository.db.CoinDao
import net.blakelee.coinprofits.repository.db.HoldingsDao
import net.blakelee.coinprofits.repository.rest.CoinApi
import net.blakelee.coinprofits.tools.toCoin
import javax.inject.Inject

class HoldingsRepository @Inject constructor(
        private val hdb: HoldingsDao,
        private val cdb: CoinDao,
        private val api: CoinApi
){

    fun insertHoldings(holdings: Holdings) = hdb.insertHoldings(holdings)

    fun updateHoldings(holdings: Holdings) = hdb.updateHoldings(holdings)

    fun getHoldings(ordered: Boolean) =
        if (ordered)
            hdb.getHoldingsOrderId()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
        else
            hdb.getHoldings()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())

    fun getHoldingsById(id: String) = hdb.getHoldingsById(id)

    fun deleteHoldings(holdings: Holdings) = hdb.deleteHoldings(holdings)

    fun refreshHoldings(convert: String) =
        hdb.getHoldings()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .toObservable()
                .take(1)
                .flatMap { items -> Observable.fromIterable(items) }
                .flatMap { old ->
                    api.getCoinById(old.id, convert)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .map { t: JsonArray -> t[0].asJsonObject.toCoin(convert) }
                            .onErrorResumeNext(Observable.empty())
                }
                .filter { it.id != "" }
                .toList()
                .doOnSuccess { cdb.updateCoins(*it.toTypedArray()) }

    fun getHoldingsCount() = hdb.getHoldingsCount()
}