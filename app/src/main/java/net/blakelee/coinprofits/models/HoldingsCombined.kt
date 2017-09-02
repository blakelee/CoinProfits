package net.blakelee.coinprofits.models

import android.arch.persistence.room.*
import net.blakelee.coinprofits.tools.decimalFormat

class HoldingsCombined : Coin() {

    var itemOrder: Long? = null

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
    fun getLast() = String.format("$%s", decimalFormat(price))
    fun getBalanceFiat() = String.format("$%s %s", decimalFormat(price * combinedAmount), currency)
    fun getBalanceCrypto() = String.format("%s %s", decimalFormat(combinedAmount), symbol)
    fun getBalanceBTC(): String = String.format("฿%s BTC", decimalFormat(combinedAmount * price_btc!!))
    fun getBalanceETH(): String = String.format("Ξ0.0 ETH")
    fun getBuyinTotal(): String = String.format("$%s", decimalFormat(averageTotal))
    fun getBuyInPrice(): String = String.format("$%s", decimalFormat(averageBuyIn))

    fun getMarginFiat(): String {
        val current: Double = averageTotal
        val buyin: Double = combinedAmount * price
        val margin: Double = buyin - current

        return if (buyin - current >= 0)
            String.format("$%.2f", margin)
        else
            String.format("-$%.2f", -margin)
    }

    fun getMarginPercent(): String {
        val buyin: Double = averageTotal
        val current: Double = combinedAmount * price
        val percent: Double = ((current / buyin) * 100) - 100
        return String.format("%.2f%%", percent)
    }
}