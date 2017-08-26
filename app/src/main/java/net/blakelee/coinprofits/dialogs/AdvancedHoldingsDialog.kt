package net.blakelee.coinprofits.dialogs

import android.content.Context
import android.view.View
import com.yarolegovich.lovelydialog.LovelyCustomDialog
import kotlinx.android.synthetic.main.dialog_merge_replace_coin.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.viewmodels.MainViewModel
import java.text.NumberFormat

/*
class AdvancedHoldingsDialog(val context: Context, val view: View, var old: Holdings, var new: Holdings, val vm: MainViewModel) {

    val dialog = LovelyCustomDialog(context)

    //view items for setting text
    val merge_amount_currency = view.merge_amount_currency
    val merge_amount_prev = view.merge_amount_prev
    val merge_amount_new = view.merge_amount_new
    val merge_amount_merged = view.merge_amount_merged
    val merge_buyin_currency = view.merge_buyin_currency
    val merge_buyin_prev = view.merge_buyin_prev
    val merge_buyin_new = view.merge_buyin_new
    val merge_buyin_merged = view.merge_buyin_merged

    val replace_amount_currency = view.replace_amount_currency
    val replace_amount_prev = view.replace_amount_prev
    val replace_amount_new = view.replace_amount_new
    val replace_buyin_currency = view.replace_buyin_currency
    val replace_buyin_prev = view.replace_buyin_prev
    val replace_buyin_new = view.replace_buyin_new

    //view items for click listeners
    val merge_item = view.merge_item
    val replace_item = view.replace_item
    val dialog_cancel = view.dialog_cancel
    val dialog_replace = view.dialog_replace
    val dialog_merge = view.dialog_merge

    //merge values
    val total: Double = old.amount + new.amount
    val p1: Double = old.amount / total
    val p2: Double = new.amount / total
    val avg: Double = (p1 * old.buyin) + (p2 * new.buyin)

    init {
        val number_format = NumberFormat.getInstance()
        number_format.minimumFractionDigits = 2
        number_format.maximumFractionDigits = 6

        merge_amount_currency.text = String.format("(%s)", old.symbol)
        merge_amount_prev.text = number_format.format(old.amount)
        merge_amount_new.text = number_format.format(new.amount)
        merge_amount_merged.text = number_format.format(total)
        merge_buyin_currency.text = String.format("(%s)", old.currency)
        merge_buyin_prev.text = number_format.format(old.buyin)
        merge_buyin_new.text = number_format.format(new.buyin)
        merge_buyin_merged.text = number_format.format(avg)
        replace_amount_currency.text = String.format("(%s)", old.symbol)
        replace_amount_prev.text = number_format.format(old.amount)
        replace_amount_new.text = number_format.format(new.amount)
        replace_buyin_currency.text = String.format("(%s)", old.currency)
        replace_buyin_prev.text = number_format.format(old.buyin)
        replace_buyin_new.text = number_format.format(new.buyin)

        merge_item.setOnClickListener {
            view.merge_expand.toggle()
            view.replace_expand.collapse()
        }

        replace_item.setOnClickListener {
            view.replace_expand.toggle()
            view.merge_expand.collapse()
        }

        dialog_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog_merge.setOnClickListener {
            merge()
            dialog.dismiss()
        }

        dialog_replace.setOnClickListener {
            replace()
            dialog.dismiss()
        }

        with(dialog) {
            setTopColorRes(R.color.colorPrimary)
            setIcon(R.drawable.ic_btc)
            setTitle("Select action for ${old.name}")
            setView(view)
            create()
        }
    }

    //Takes the previous amount and buy-in value and merges it with new values to get the expected value
    private fun merge() {
        old.amount = total
        old.buyin = avg
        vm.updateHoldings(old)
    }

    private fun replace() {
        old.amount = new.amount
        old.buyin = new.buyin
        vm.updateHoldings(old)
    }

    fun show() { dialog.show() }
}*/