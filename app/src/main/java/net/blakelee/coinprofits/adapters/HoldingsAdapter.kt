package net.blakelee.coinprofits.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.coin_item_main.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.BaseAdapter
import net.blakelee.coinprofits.base.BaseViewHolder
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.tools.decimalFormat
import net.cachapa.expandablelayout.ExpandableLayout

class HoldingsAdapter(val recyclerView: RecyclerView, val picasso: Picasso, val longClick: (Holdings) -> Unit) : BaseAdapter<Holdings, HoldingsAdapter.HoldingsViewHolder>() {

    private val UNSELECTED = -1
    private var selectedItem: Int = UNSELECTED

    override fun getItemViewId(): Int = R.layout.coin_item_main

    override fun instantiateViewHolder(view: View?): HoldingsViewHolder {
        return HoldingsViewHolder(view)
    }

    inner class HoldingsViewHolder(view: View?) : BaseViewHolder<Holdings>(view) {
        private val expandButton: RelativeLayout = itemView.item_top
        private val expandableLayout: ExpandableLayout = itemView.coin_item_expand
        private val coin_icon: ImageView = itemView.coin_icon
        private val name: TextView = itemView.name
        private val last_price: TextView = itemView.last_price
        private val balance: TextView = itemView.balance
        private val balance_symbol: TextView = itemView.balance_symbol
        private val balance_currency: TextView = itemView.balance_currency
        private val balance_btc: TextView = itemView.balance_btc
        private val balance_eth: TextView = itemView.balance_eth
        private val buyin_price: TextView = itemView.buyin
        private val margin_value: TextView = itemView.margin_value
        private val margin_percent: TextView = itemView.margin_percent

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
            last_price.text = String.format("$%s", decimalFormat(price))
            balance.text = String.format("$%s", decimalFormat(cur))

            //Balance
            balance_symbol.text = String.format("%s %s", decimalFormat(item.amount), item.symbol)
            balance_currency.text = String.format("$%s %s", decimalFormat(cur), item.currency)
            balance_btc.text = String.format("฿%s BTC", decimalFormat(item.price_btc!! * item.amount))
            balance_eth.text = String.format("Ξ0.0 ETH")

            //Buyin Price
            buyin_price.text = decimalFormat(item.buyin)

            //Margins
            margin_value.text = if (curbuy > 0) String.format("$%.2f", curbuy)
            else String.format("-$%.2f", -curbuy)
            margin_percent.text = String.format("%.2f%%", buycur)

            expandButton.setOnLongClickListener {
                longClick(item)
                true
            }

            expandButton.setOnClickListener {
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

            expandableLayout.setOnExpansionUpdateListener { expansionFraction, state ->
                recyclerView.smoothScrollToPosition(adapterPosition)
            }
        }
    }
}
