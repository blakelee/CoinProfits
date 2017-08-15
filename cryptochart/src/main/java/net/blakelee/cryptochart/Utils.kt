package net.blakelee.cryptochart

import java.text.NumberFormat

val Float.decimalFormat : String
 get() {
    val format = NumberFormat.getInstance()
    val splitter = "%f".format(this).split("\\.")

    format.minimumFractionDigits = 2

    if (this > 1)
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

    return format.format(this)
}

val Double.decimalFormat : String
    get() = this.toFloat().decimalFormat