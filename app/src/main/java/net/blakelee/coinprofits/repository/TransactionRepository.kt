package net.blakelee.coinprofits.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.models.Transaction
import net.blakelee.coinprofits.repository.db.TransactionDao
import net.blakelee.coinprofits.repository.rest.ERC20Api
import java.lang.Math.pow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
        private val db: TransactionDao,
        private val api: ERC20Api
){

    //Gets a list of tokens associated with the address
    fun getAddressInfo(address: String) = api.getAddressInfo(address)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { erc20 ->
                erc20.error?.let { throw RuntimeException("Invalid address") }

                val transactions: MutableMap<String, Double> = mutableMapOf()

                erc20.eTH?.let { it.balance?.let { transactions["ETH"] = it.toDouble() } }

                erc20.tokens?.forEach {
                    if (it.tokenInfo != null && it.balance != null && it.tokenInfo!!.symbol != null)
                        //I have no idea why it's raised to the 18th power
                        transactions[it.tokenInfo!!.symbol!!] = it.balance!! / pow(10.toDouble(), 18.toDouble())
                }

                transactions
            }/*
            .doOnNext { map ->
                db.getTransactionsByAddress(address)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        .take(1)
                        .subscribe {
                            //Update the values of each transaction
                            it.forEach {
                                if (map.containsKey(it.id))
                                    it.amount = map.getOrDefault(it.id, 0.0)
                            }
                            Observable.fromCallable {
                                db.insertTransactions(it)
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe()//Put the updated Transactions inside the db again
                        }
            }*/

    fun insertTransactions(transactions: List<Transaction>) =
            Observable.fromCallable {
                db.insertTransactions(transactions)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())


    fun deleteTransactionsById(id: String) =
            Observable.fromCallable {
                db.deleteTransactionsById(id)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
}