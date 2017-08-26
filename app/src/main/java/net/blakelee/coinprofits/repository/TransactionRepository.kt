package net.blakelee.coinprofits.repository

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.repository.db.TransactionDao
import net.blakelee.coinprofits.repository.rest.ERC20Api
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
                        transactions[it.tokenInfo!!.symbol!!] = it.balance!!
                }

                transactions
            }
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
                            db.insertTransactions(it) //Put the updated Transactions inside the db again
                        }

            }
}