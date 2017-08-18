package net.blakelee.coinprofits.repository

import net.blakelee.coinprofits.repository.rest.ERC20Api
import javax.inject.Inject

class ERC20Repository @Inject constructor(
        private val api: ERC20Api
){

}