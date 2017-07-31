package net.blakelee.coinprofits.pricechart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter(val period: Period) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val current: Locale = Locale.getDefault()

        when (period) {
            Period.HOUR, Period.DAY -> return SimpleDateFormat("HH:mm", current).format(value)
            Period.WEEK -> return SimpleDateFormat("EEE", current).format(value)
            Period.MONTH -> return SimpleDateFormat("M/d", current).format(value)
            Period.YEAR -> return SimpleDateFormat("MMM", current).format(value)
            Period.ALL -> return SimpleDateFormat("M/YY", current).format(value)
        }
    }
}

fun TimeFormatter(period: Period): Long {
    return when(period) {
        Period.HOUR -> 1000 * 60 * 60
        Period.DAY -> 1000 * 60 * 60 * 24
        Period.WEEK -> 1000 * 60 * 60 * 24 * 7
        Period.MONTH -> (1000 * 60 * 60 * 24 * 30.4375).toLong()
        Period.YEAR -> (1000 * 60 * 60 * 24 * 365.25).toLong()
        Period.ALL -> 500_000_000_000
    }
}

enum class Period (val value: Int) {
    HOUR(0),
    DAY(1),
    WEEK(2),
    MONTH(3),
    YEAR(4),
    ALL(5)
}