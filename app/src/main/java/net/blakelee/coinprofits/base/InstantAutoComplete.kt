package net.blakelee.coinprofits.base

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.AutoCompleteTextView

class InstantAutoComplete : AutoCompleteTextView {

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttribute: Int): super(context, attributeSet, defStyleAttribute)

    var force: Boolean = false

    override fun enoughToFilter(): Boolean {
        return if (force)
            true
        else
            text.length >= threshold
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (focused)
            performFiltering(text, 0)
    }
}