package net.blakelee.coinprofits.repository

import com.google.gson.JsonArray
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.models.HoldingsCombined
import net.blakelee.coinprofits.models.HoldingsOverview
import net.blakelee.coinprofits.models.HoldingsTransactions
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

    //Used in
    fun insertHoldings(holdings: Holdings) =
            Observable.fromCallable {
                hdb.insertHoldings(holdings)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    //Used to determine whether to display the last updated indicator in the main fragment
    fun getHoldingsCount(): Flowable<Int> = hdb.getHoldingsCount()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    //The holdings containing transactions used in the add holdings activity
    fun getHoldingsTransactions(id: String): Maybe<HoldingsTransactions> = hdb.getHoldingsTransactions(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    //The big combined holdings class used in the Main fragment
    fun getHoldingsCombined(): Flowable<List<HoldingsCombined>> = hdb.getHoldingsCombined()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    //This is just the id, name, symbol, and order used in the Overview fragment
    fun getHoldingsOverview(): Flowable<List<HoldingsOverview>> = hdb.getHoldingsOverview()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    fun getHoldings(): Flowable<List<Holdings>> = hdb.getHoldings()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    //Used for when changing the item order of the holdings
    fun updateHoldings(vararg holdings: Holdings) =
            Observable.fromCallable {
                hdb.updateHoldings(*holdings)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

    fun updateHoldings(holdings: List<Holdings>) = updateHoldings(*holdings.toTypedArray())

    //Automatically deletes transactions since they are relations
    fun deleteHoldings(holdings: Holdings) =
            Observable.fromCallable {
                hdb.deleteHoldings(holdings)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())

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
                .doOnSuccess {
                    Observable.fromCallable {
                        cdb.updateCoins(*it.toTypedArray())
                    }
                }
}