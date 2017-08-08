package net.blakelee.coinprofits.repository

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.blakelee.coinprofits.repository.rest.ChartApi
import javax.inject.Inject

class ChartRepository @Inject constructor(
        val api: ChartApi
) {

    fun getChartData(id: String, start: Long, end: Long) =
        api.getChart(id, start, end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Log.i("RETROFIT", it.localizedMessage) }

}