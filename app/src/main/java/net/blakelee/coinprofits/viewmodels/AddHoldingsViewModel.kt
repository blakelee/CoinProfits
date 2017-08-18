package net.blakelee.coinprofits.viewmodels

import android.arch.lifecycle.ViewModel
import net.blakelee.coinprofits.repository.CoinRepository
import net.blakelee.coinprofits.repository.ERC20Repository
import net.blakelee.coinprofits.repository.HoldingsRepository
import javax.inject.Inject

class AddHoldingsViewModel @Inject constructor(
        private val ERC20Repo: ERC20Repository,
        private val HoldingsRepo: HoldingsRepository,
        private val CoinRepo: CoinRepository
) : ViewModel() {

    fun getCoins() = CoinRepo.coins

    fun getHoldingsById(id: String) {}
}