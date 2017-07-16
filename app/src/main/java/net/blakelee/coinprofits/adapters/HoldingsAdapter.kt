package net.blakelee.coinprofits.adapters

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.coin_item_main.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.BaseAdapter
import net.blakelee.coinprofits.base.BaseViewHolder
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.tools.toBitmap
import net.cachapa.expandablelayout.ExpandableLayout
import java.text.NumberFormat

class HoldingsAdapter(val longClick: (Holdings) -> Unit) : BaseAdapter<Holdings, HoldingsAdapter.HoldingsViewHolder>() {

    override fun getItemViewId(): Int = R.layout.coin_item_main

    override fun instantiateViewHolder(view: View?): HoldingsViewHolder = HoldingsViewHolder(view, longClick)

    class HoldingsViewHolder(view: View?, val longClick: (Holdings) -> Unit) : BaseViewHolder<Holdings>(view) {
        val item_top: LinearLayout = view!!.item_top
        val coin_icon: ImageView = view!!.coin_icon
        val name: TextView = view!!.name
        val last_price: TextView = view!!.last_price
        val coin_item_expand: ExpandableLayout = view!!.coin_item_expand
        val balance: TextView = view!!.balance
        val balance_symbol: TextView = view!!.balance_symbol
        val balance_currency: TextView = view!!.balance_currency
        val balance_btc: TextView = view!!.balance_btc
        val balance_eth: TextView = view!!.balance_eth
        val buyin_price: TextView = view!!.buyin
        val margin_value: TextView = view!!.margin_value
        val margin_percent: TextView = view!!.margin_percent

        override fun onBind(item: Holdings) {
            val buyin = (item.amount * item.buyin)
            val cur = (item.amount * item.price!!)
            val buycur = ((cur / buyin) * 100) - 100
            val curbuy = cur - buyin
            val price: Double = item.price!!

            //Item
            name.text = item.name
            coin_icon.setImageBitmap(item.image?.toBitmap())


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

            //Click listener for when you click the item but not the expandable part
            item_top.setOnClickListener {
                coin_item_expand.toggle()
            }

            item_top.setOnLongClickListener {
                longClick(item)
                true
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