package net.blakelee.coinprofits.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.RelativeLayout
import com.robertlevonyan.views.chip.Chip
import net.blakelee.coinprofits.R

fun Chip(context: Context, bitmap: Bitmap?, text: String): Chip {
    val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
    params.leftMargin = 12
    params.topMargin = 4
    val chip = Chip(context)
    chip.chipIcon = BitmapDrawable(context.resources, bitmap)
    chip.chipText = text
    chip.isHasIcon = true
    chip.isClickable = true
    chip.isClosable = true
    chip.id = R.id.chip
    chip.layoutParams = params
    return chip
}