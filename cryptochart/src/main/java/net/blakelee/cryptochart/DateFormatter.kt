package net.blakelee.cryptochart

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