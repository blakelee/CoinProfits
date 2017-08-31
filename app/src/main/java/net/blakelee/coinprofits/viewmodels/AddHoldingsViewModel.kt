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

    fun getCoins() = coinRepo.coins

    fun getTransactionHoldings(id: String) = holdingsRepo.getTransactionHoldings(id)

    fun insertHoldings(holdings: Holdings) = holdingsRepo.insertHoldings(holdings)

    fun insertTransactions(transactions: List<Transaction>) = transactionRepo.insertTransactions(transactions)

    fun deleteTransactionsById(id: String) = transactionRepo.deleteTransactionsById(id)

    fun getAddressInfo(address: String) = transactionRepo.getAddressInfo(address)
}