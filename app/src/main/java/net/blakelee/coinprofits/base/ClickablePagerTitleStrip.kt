package net.blakelee.coinprofits.base

import android.content.Context
import android.support.v4.view.PagerTitleStrip
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class ClickablePagerTitleStrip(context: Context, attributeSet: AttributeSet) : PagerTitleStrip(context, attributeSet) {

    private val previous by lazy { getChildAt(0) }
    private val next by lazy { getChildAt(2) }
    private var pager: ViewPager? = null

    init {
        previous.setOnClickListener {
            pager?.let {
                if (it.currentItem != 0)
                    it.currentItem = it.currentItem - 1
            }
        }

        next.setOnClickListener {
            pager?.let {
                if (it.currentItem != it.adapter.count - 1)
                    it.currentItem = it.currentItem + 1
            }
        }

        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (parent is ViewPager)
            pager = parent as ViewPager
        else
            throw IllegalStateException("ClickablePagerTitleStrip must be a direct child of a ViewPager")
    }
}