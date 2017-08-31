package net.blakelee.coinprofits.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import net.blakelee.coinprofits.tools.decimalFormat

class MainCombined {
    @Embedded
    lateinit var holdings: Holdings

    //@Relation(parentColumn = "id", entityColumn = "id", entity = Coin::class)
    //lateinit var coin: Coin

    @Relation(parentColumn = "id", entityColumn = "id", entity = Transaction::class)
    var transaction: List<Transaction> = emptyList()

    @Ignore
    private var combinedAmount: Double = -1.0
    get() {
        if (field < 0.0)
            field = transaction.sumByDouble { it.amount }
        return field
    }

    @Ignore
    private var averageBuyIn: Double = -1.0
    get() {
        if (field < 0.0) {
            field =
            if (transaction.isNotEmpty())
               transaction.sumByDouble { (it.amount * it.price) / combinedAmount }
            else
                0.0
        }
        return field
    }

    @Ignore
    private var averageTotal: Double = -1.0
    get() {
        if (field < 0.0) {
            field =
            if(transaction.isNotEmpty())
                transaction.sumByDouble { it.amount * it.price }
            else
                0.0
        }
        return field
    }

    fun watchOnly(): Boolean = transaction.isEmpty()
    fun getLast() = String.format("$%s", decimalFormat(if (holdings.price != null) holdings.price!! else 0.0))
    fun getBalanceFiat() = String.format("$%s %s", decimalFormat(if (holdings.price != null) holdings.price!! * combinedAmount else 0.0), holdings.currency)
    fun getBalanceCrypto() = String.format("%s %s", decimalFormat(combinedAmount), holdings.symbol)
    fun getBalanceBTC(): String = String.format("฿%s BTC", decimalFormat(if (holdings.price_btc != null) combinedAmount * holdings.price_btc!! else 0.0))
    fun getBalanceETH(): String = String.format("Ξ0.0 ETH")
    fun getBuyinTotal(): String = String.format("$%s", decimalFormat(averageTotal))
    fun getBuyInPrice(): String = String.format("$%s", decimalFormat(averageBuyIn))

    fun getMarginFiat(): String {
        val current: Double = averageTotal
        val buyin: Double = combinedAmount * holdings.price!!
        val margin: Double = buyin - current

        return if (buyin - current >= 0)
            String.format("$%.2f", margin)
        else
            String.format("-$%.2f", -margin)
    }

    fun getMarginPercent(): String {
        val buyin: Double = averageTotal
        val current: Double = combinedAmount * holdings.price!!
        val percent: Double = ((current / buyin) * 100) - 100
        return String.format("%.2f%%", percent)
    }
}