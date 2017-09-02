package net.blakelee.coinprofits.viewmodels

import android.arch.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.models.Transaction
import net.blakelee.coinprofits.repository.CoinRepository
import net.blakelee.coinprofits.repository.HoldingsRepository
import net.blakelee.coinprofits.repository.TransactionRepository
import javax.inject.Inject

class AddHoldingsViewModel @Inject constructor(
        private val transactionRepo: TransactionRepository,
        private val holdingsRepo: HoldingsRepository,
        private val coinRepo: CoinRepository,
        val picasso: Picasso
) : ViewModel() {

    //The coins for the auto complete text view
    fun getCoins() = coinRepo.coins

    //Get the transactions for the holdings that the user has selected
    fun getHoldingsTransactions(id: String) = holdingsRepo.getHoldingsTransactions(id)

    fun insertHoldings(holdings: Holdings) = holdingsRepo.insertHoldings(holdings)

    fun insertTransactions(transactions: List<Transaction>) = transactionRepo.insertTransactions(transactions)

    //When a user changes holdings, drop the table and insert all the new values
    fun deleteTransactionsById(id: String) = transactionRepo.deleteTransactionsById(id)

    fun getAddressInfo(address: String) = transactionRepo.getAddressInfo(address)
}