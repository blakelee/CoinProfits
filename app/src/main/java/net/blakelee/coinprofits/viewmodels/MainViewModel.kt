package net.blakelee.coinprofits.viewmodels

import android.arch.lifecycle.*
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.models.HoldingsCombined
import net.blakelee.coinprofits.repository.CoinRepository
import net.blakelee.coinprofits.repository.HoldingsRepository
import net.blakelee.coinprofits.repository.PreferencesRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainViewModel
@Inject constructor (
        private val prefs: PreferencesRepository,
        private val coinRepo: CoinRepository,
        private val holdingsRepo: HoldingsRepository
) : ViewModel() {

    val isRefreshing: Subject<Boolean> = PublishSubject.create()
    var holdingsCombined = holdingsRepo.getHoldingsCombined()
    var count = ObservableInt()
    var lastUpdated = ObservableField<String>()


    fun checkPreferences() {
        if (prefs.download) {
            prefs.download = false
            isRefreshing.onNext(true)
            coinRepo.deleteAllCoins().subscribe {
                coinRepo.getCoins(prefs.convert)
                        .subscribe {
                            prefs.lastUpdated.set(getTime())
                            isRefreshing.onNext(false)
                        }
            }
        }

        if (prefs.autoRefresh)
            refreshHoldings().subscribe()
    }

    fun updateHoldings(holdingsCombined: List<HoldingsCombined>) {
        val holdings: MutableList<Holdings> = mutableListOf()

        holdingsCombined.forEachIndexed { index, single ->
            val item = Holdings()
            item.id = single.id
            item.order = index.toLong() + 1
            holdings.add(item)
        }

        holdingsRepo.updateHoldings(holdings)
    }

    fun refreshHoldings(): Single<MutableList<Coin>> {
        prefs.lastUpdated.set(getTime())
        return holdingsRepo.refreshHoldings(prefs.convert)
    }
    fun getLastUpdated() = prefs.lastUpdated.asObservable()
    fun getCount() = holdingsRepo.getHoldingsCount()
    fun deleteHoldings(holdings: Holdings) = holdingsRepo.deleteHoldings(holdings)
    fun setCount(count: Int) = this.count.set(count)
    fun setLastUpdated(lastUpdated: String) = this.lastUpdated.set(lastUpdated)
    private fun getTime(): String = SimpleDateFormat("h:mma", Locale.getDefault()).format(Date())
}
