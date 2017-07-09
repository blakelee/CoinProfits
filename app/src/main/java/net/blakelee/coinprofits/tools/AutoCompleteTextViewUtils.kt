package net.blakelee.coinprofits.tools

import android.widget.AutoCompleteTextView

fun AutoCompleteTextView.disableText() {
    this.dismissDropDown()
    this.clearListSelection()
    this.clearFocus()
    this.setText("")
    this.hint = ""
    this.isEnabled = false
}

fun AutoCompleteTextView.enableText(hint: String) {
    this.isEnabled = true
    this.hint = hint
}