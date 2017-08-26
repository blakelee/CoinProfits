package net.blakelee.coinprofits.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RelativeLayout
import com.robertlevonyan.views.chip.Chip
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.yarolegovich.lovelydialog.LovelyCustomDialog
import kotlinx.android.synthetic.main.dialog_add_coin.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.adapters.AutoCompleteCurrencyAdapter
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.tools.disableText
import net.blakelee.coinprofits.tools.enableText
import net.blakelee.coinprofits.viewmodels.MainViewModel
/*
class HoldingsDialog(val context: Context, val view: View, var holdings: Holdings?, val vm: MainViewModel, val picasso: Picasso, val mr_cb: (Holdings, Holdings) -> Unit ) {

    var items: List<Coin> = listOf()
    var dialog = LovelyCustomDialog(context)
    val adapter by lazy { AutoCompleteCurrencyAdapter(context, picasso) }
    val coin = view.coin
    val search_parent = view.search_parent
    val save = view.dialog_save
    val cancel = view.dialog_cancel
    val amount = view.amount
    val buyin = view.buyin
    var chip: Chip? = null
    var item: Coin? = null

    init {

        vm.coins.subscribe {
            adapter.originalList = it
            items = it
        }

        fun Chip(): Chip {
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = 12
            params.topMargin = 4
            val chip = Chip(context)
            picasso.load(AppModule.IMAGE_URL + item!!.id + ".png").into(object : Target {
                override fun onBitmapFailed(errorDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    chip.chipIcon = BitmapDrawable(context.resources, bitmap)
                }
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })

            chip.chipText = item!!.name
            chip.isHasIcon = true
            chip.isClickable = true
            chip.isClosable = true
            chip.id = R.id.chip
            chip.layoutParams = params
            return chip
        }

        //Editing
        holdings?.let {
            val pos = items.binarySearchBy(holdings!!.id) { it.id }
            item = items[pos]
            amount.setText(it.amount.toString())
            buyin.setText(it.buyin.toString())
            coin.disableText()
            chip = Chip()
            chip!!.isClosable = false //If we're editing we can't close the chip
            search_parent.addView(chip)
        }

        coin.setAdapter(adapter)

        //Listeners
        coin.setOnItemClickListener { _, _, position, _ ->

            item = adapter.getItem(position)

            chip = Chip()
            chip!!.setOnCloseClickListener {
                coin.enableText(context.resources.getString(R.string.coin))
                search_parent.removeView(chip)
            }

            search_parent.addView(chip)
            coin.disableText()
        }

        save.setOnClickListener {

            val validation = validate(chip, item, amount.text.toString().toDoubleOrNull(), buyin.text.toString().toDoubleOrNull(), holdings)

            when(validation) {
                Validation.NO_CHIP -> coin.error = context.resources.getString(R.string.no_coin)
                Validation.NO_AMOUNT -> amount.error = context.resources.getString(R.string.no_amount)
                Validation.NO_BUYIN -> buyin.error = context.resources.getString(R.string.no_buyin)
                Validation.NORMAL -> {
                    save(item!!, amount.text.toString().toDouble(), buyin.text.toString().toDouble())
                    dialog.dismiss()
                }
                Validation.UPDATE -> {
                    update(holdings!!, amount.text.toString().toDouble(), buyin.text.toString().toDouble())
                    dialog.dismiss()
                }
                Validation.ADVANCED -> {
                    dialog.dismiss()
                    advanced()
                }
            }
        }

        cancel.setOnClickListener { dialog.dismiss() }

        //Make dialog
        with(dialog) {
            setTopColorRes(R.color.colorPrimary)
            setIcon(R.drawable.ic_btc)
            setTitle(R.string.dialog_add)
            setView(view)
            create()
        }
    }

    fun show() {
        this.dialog.show()
    }

    private fun advanced() {
        val new: Holdings = Holdings()
        new.id = item!!.id
        new.amount = amount.text.toString().toDouble()
        new.buyin = buyin.text.toString().toDouble()
        mr_cb(holdings!!, new)
    }

    private fun update(holdings: Holdings, amount: Double, buyin: Double) {
        holdings.buyin = buyin
        holdings.amount = amount
        vm.updateHoldings(holdings)
    }

    private fun save(coin: Coin, _amount: Double, _buyin: Double) {
        val holdings = Holdings()
        with(holdings) {
            id = coin.id
            amount = _amount
            buyin = _buyin
            name = coin.name
            symbol = coin.symbol
        }

        vm.insertHoldings(holdings)
    }

    private fun validate(chip: Chip?, coin: Coin?, amount: Double?, buyin: Double?, holdings: Holdings?): Validation {
        if (chip == null)
            return Validation.NO_CHIP

        if (amount == null)
            return Validation.NO_AMOUNT

        if (buyin == null)
            return Validation.NO_BUYIN

        holdings?.let { return Validation.UPDATE }

        coin?.let {
            val dup: Holdings? = vm.getHoldingsById(coin.id).blockingGet()
            dup?.let {
                this.holdings = dup
                return Validation.ADVANCED
            }
        }

        return Validation.NORMAL
    }

    private enum class Validation {
        NO_CHIP,
        NO_AMOUNT,
        NO_BUYIN,
        NORMAL,
        ADVANCED,
        UPDATE
    }
}*/