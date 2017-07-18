package net.blakelee.coinprofits.service.repository

import net.blakelee.coinprofits.di.modules.NetworkModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinMarketCapRepository @Inject constructor(private val networkModule: NetworkModule) {

}