package net.blakelee.coinprofits.adapters

import android.content.Context
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
import net.blakelee.coinprofits.models.MainCombined
import net.cachapa.expandablelayout.ExpandableLayout

class MainCombinedAdapter(val recyclerView: RecyclerView, val picasso: Picasso, val context: Context, val longClick: (MainCombined) -> Unit) : BaseAdapter<MainCombined, MainCombinedAdapter.MainCombinedViewHolder>() {

    private val UNSELECTED = -1
    private var selectedItem: Int = UNSELECTED

    override fun getItemViewId(): Int = R.layout.coin_item_main

    override fun instantiateViewHolder(view: View?): MainCombinedViewHolder = MainCombinedViewHolder(view)

    inner class MainCombinedViewHolder(view: View?) : BaseViewHolder<MainCombined>(view) {
        private val expandButton: RelativeLayout = itemView.item_top
        private val expandableLayout: ExpandableLayout = itemView.coin_item_expand
        private val coin_icon: ImageView = itemView.coin_icon
        private val name: TextView = itemView.name
        private val last_price: TextView = itemView.last_price
        private val balance_text: TextView = itemView.balance_text
        private val balance: TextView = itemView.balance
        private val balance_symbol: TextView = itemView.balance_symbol
        private val balance_currency: TextView = itemView.balance_currency
        private val balance_btc: TextView = itemView.balance_btc
        private val balance_eth: TextView = itemView.balance_eth
        private val buyin_price: TextView = itemView.buyin_average
        private val buyin_total: TextView = itemView.buyin_total
        private val margin_value: TextView = itemView.margin_value
        private val margin_percent: TextView = itemView.margin_percent

        override fun onBind(item: MainCombined) {
            expandButton.isSelected = false
            expandableLayout.collapse(false)

            //Item
            name.text = item.holdings.name
            last_price.text = item.getLast()
            picasso.load(AppModule.IMAGE_URL + item.holdings.id+ ".png").into(coin_icon)

            if (item.watchOnly()) {
                //Item
                balance_text.visibility = View.GONE
                balance.text = context.getText(R.string.read_only)
            } else {
                //Item
                balance.text = item.getBalanceFiat()

                //Balance
                balance_symbol.text = item.getBalanceFiat()
                balance_currency.text = item.getBalanceCrypto()
                balance_btc.text = item.getBalanceBTC()
                balance_eth.text = item.getBalanceETH()

                //Buyin Price
                buyin_total.text = item.getBuyinTotal()
                buyin_price.text = item.getBuyInPrice()

                //Margins
                margin_value.text = item.getMarginFiat()
                margin_percent.text = item.getMarginPercent()

                expandButton.setOnClickListener {
                    val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as MainCombinedViewHolder?
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
            }

            expandButton.setOnLongClickListener {
                longClick(item)
                true
            }

            expandableLayout.setOnExpansionUpdateListener { _, _ ->
                recyclerView.smoothScrollToPosition(adapterPosition)
            }
        }
    }
}
