package net.blakelee.coinprofits.base

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent



class SwipeViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private var swipeable: Boolean = true

    fun setSwipeable(swipeable: Boolean) {
        this.swipeable = swipeable
    }

    override fun onInterceptTouchEvent(me: MotionEvent): Boolean {
        return if (swipeable) super.onInterceptTouchEvent(me) else false
    }
}