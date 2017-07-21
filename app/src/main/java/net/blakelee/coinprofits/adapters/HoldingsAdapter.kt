package net.blakelee.coinprofits.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.coin_item_main.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.BaseAdapter
import net.blakelee.coinprofits.base.BaseViewHolder
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.Holdings
import net.cachapa.expandablelayout.ExpandableLayout
import java.text.NumberFormat

class HoldingsAdapter(val recyclerView: RecyclerView, val picasso: Picasso, val longClick: (Holdings) -> Unit) : BaseAdapter<Holdings, HoldingsAdapter.HoldingsViewHolder>() {

    private val UNSELECTED = -1
    private var selectedItem: Int = UNSELECTED

    override fun getItemViewId(): Int = R.layout.coin_item_main

    override fun instantiateViewHolder(view: View?): HoldingsViewHolder {
        return HoldingsViewHolder(view)
    }

    inner class HoldingsViewHolder(view: View?) : BaseViewHolder<Holdings>(view), ExpandableLayout.OnExpansionUpdateListener, View.OnClickListener {
        private val expandButton: LinearLayout = view!!.item_top
        private val expandableLayout: ExpandableLayout = view!!.coin_item_expand
        private val coin_icon: ImageView = view!!.coin_icon
        private val name: TextView = view!!.name
        private val last_price: TextView = view!!.last_price
        private val balance: TextView = view!!.balance
        private val balance_symbol: TextView = view!!.balance_symbol
        private val balance_currency: TextView = view!!.balance_currency
        private val balance_btc: TextView = view!!.balance_btc
        private val balance_eth: TextView = view!!.balance_eth
        private val buyin_price: TextView = view!!.buyin
        private val margin_value: TextView = view!!.margin_value
        private val margin_percent: TextView = view!!.margin_percent

        init {
            expandableLayout.setOnExpansionUpdateListener(this)
            expandButton.setOnClickListener(this)
        }

        override fun onBind(item: Holdings) {
            expandButton.isSelected = false
            expandableLayout.collapse(false)

            val buyin = (item.amount * item.buyin)
            val cur = (item.amount * item.price!!)
            val buycur = ((cur / buyin) * 100) - 100
            val curbuy = cur - buyin
            val price: Double = item.price!!

            //Item
            name.text = item.name
            picasso.load(AppModule.IMAGE_URL + item.id + ".png").into(coin_icon)
            last_price.text = String.format("$%s", format(price))
            balance.text = String.format("$%s", format(cur))

            //Balance
            balance_symbol.text = String.format("%s %s", format(item.amount), item.symbol)
            balance_currency.text = String.format("$%s %s", format(cur), item.currency)
            balance_btc.text = String.format("฿%s BTC", format(item.price_btc!! * item.amount))
            balance_eth.text = String.format("Ξ0.0 ETH")

            //Buyin Price
            buyin_price.text = format(item.buyin)

            //Margins
            margin_value.text = if (curbuy > 0) String.format("$%.2f", curbuy)
                else String.format("-$%.2f", -curbuy)
            margin_percent.text = String.format("%.2f%%", buycur)

            expandButton.setOnLongClickListener {
                longClick(item)
                true
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            recyclerView.smoothScrollToPosition(adapterPosition)
        }

        override fun onClick(view: View) {
            val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as HoldingsViewHolder?
            holder?.let {
                holder.expandButton.isSelected = false
                holder.expandableLayout.collapse()
            }

            if (adapterPosition == selectedItem) {
                selectedItem = UNSELECTED
            } else {
                expandButton.isSelected = true
                expandableLayout.expand()
                selectedItem = adapterPosition
            }
        }

        private fun format(number: Double): String {
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
    }
}
