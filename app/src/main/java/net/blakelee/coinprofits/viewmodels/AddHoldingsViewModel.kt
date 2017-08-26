package net.blakelee.coinprofits.viewmodels

import android.arch.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import net.blakelee.coinprofits.models.Holdings
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

    fun getHoldingsById(id: String) = holdingsRepo.getHoldingsById(id)

    fun insertHoldings(holdings: Holdings) = holdingsRepo.insertHoldings(holdings)
}