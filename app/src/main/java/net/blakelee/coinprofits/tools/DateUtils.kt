package net.blakelee.coinprofits.tools

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter(val period: Period) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val current: Locale = Locale.getDefault()

        when (period) {
            Period.DAY -> return SimpleDateFormat("HH:mm", current).format(value)
            Period.WEEK -> return SimpleDateFormat("EEE", current).format(value)
            Period.MONTH,
            Period.QUARTER,
            Period.SEMESTER -> return SimpleDateFormat("M/d", current).format(value)
            Period.YEAR -> return SimpleDateFormat("MMM", current).format(value)
            Period.ALL -> return SimpleDateFormat("M/YY", current).format(value)
        }
    }
}

fun TimeFormatter(period: Period): Long {
    return when(period) {
        Period.DAY -> 1000 * 60 * 60 * 24
        Period.WEEK -> 1000 * 60 * 60 * 24 * 7
        Period.MONTH -> (1000 * 60 * 60 * 24 * 30.4375).toLong()
        Period.QUARTER -> (1000 * 60 * 60 * 24 * 30.4375 * 3).toLong()
        Period.SEMESTER -> (1000 * 60 * 60 * 24 * 30.4375 * 6).toLong()
        Period.YEAR -> (1000 * 60 * 60 * 24 * 365.25).toLong()
        Period.ALL -> 500_000_000_000
    }
}

fun SinceFormatter(period: Period, since: Float = 0f): String {
    return when(period) {
        Period.DAY -> "Since yesterday"
        Period.WEEK -> "Since last week"
        Period.MONTH -> "Since last month"
        Period.QUARTER -> "Since 3 months ago"
        Period.SEMESTER -> "Since 6 months ago"
        Period.YEAR -> "Since last year"
        Period.ALL -> "Since " + SimpleDateFormat("MMMM, YYYY", Locale.getDefault()).format(since)
    }
}

enum class Period (val value: Int) {
    DAY(0),
    WEEK(1),
    MONTH(2),
    QUARTER(3),
    SEMESTER(4),
    YEAR(5),
    ALL(6)
}

fun LabelCount(period: Period): Pair<Int, Boolean> {
    return when (period) {
        Period.DAY -> Pair(7, true)
        Period.WEEK,
        Period.MONTH -> Pair(8, true)
        Period.QUARTER,
        Period.SEMESTER -> Pair(7, true)
        Period.YEAR -> Pair(13, true)
        Period.ALL -> Pair(7, false)
    }
}