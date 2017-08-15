package net.blakelee.coinprofits.tools

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import java.text.NumberFormat

val Int.dp: Float
        get() = (this / Resources.getSystem().displayMetrics.density + 0.5f)

val Int.px: Float
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f) //Round to ceil

fun textDim(text: String, context: Context, textSize: Float = 16f): Int {
    val bounds = Rect()
    val textPaint = Paint()
    textPaint.typeface = Typeface.DEFAULT
    textPaint.textSize = textSize
    textPaint.getTextBounds(text, 0, text.length, bounds)
    return bounds.width() * context.resources.displayMetrics.density.toInt()
}

fun decimalFormat(number: Double): String {
    val format = NumberFormat.getInstance()
    val splitter = "%f".format(number).split("\\.")

    format.minimumFractionDigits = 2

    if (number > 1)
        format.maximumFractionDigits = 2
    else {
        var places = 0

        if (splitter[0].length > 2)
            for(char in splitter[0]) {
                if (char != '0' && places >= 2) {
                    break
                }
                places++
            }
        else {
            places = 2
        }

        format.maximumFractionDigits = places
    }

    return format.format(number)
}