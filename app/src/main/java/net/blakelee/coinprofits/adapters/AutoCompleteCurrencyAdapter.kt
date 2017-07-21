package net.blakelee.coinprofits.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.Coin

class AutoCompleteCurrencyAdapter(context: Context, private val currency: List<Coin>, private val picasso: Picasso) : ArrayAdapter<Coin>(context, 0, currency) {
    val filteredCurrency: ArrayList<Coin> = ArrayList()

    override fun getCount() = filteredCurrency.size
    override fun getFilter(): Filter = CoinFilter(this, currency)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item from filtered list.
        val coin = filteredCurrency[position]

        val inflater = LayoutInflater.from(context)
        var view: View? = convertView

        if (view == null)
            view = inflater.inflate(R.layout.dialog_item_coin, parent, false)

        val text = view!!.findViewById(R.id.coin_text) as TextView
        val icon = view.findViewById(R.id.coin_icon) as ImageView
        text.text = String.format("%s - %s", coin.symbol, coin.name)

        picasso.load(AppModule.IMAGE_URL + coin.id + ".png").into(icon)

        return view
    }
}

internal class CoinFilter(var adapter: AutoCompleteCurrencyAdapter, var originalList: List<Coin>) : Filter() {

    var filteredList: ArrayList<Coin> = ArrayList()

    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        filteredList.clear()
        val results = Filter.FilterResults()

        if (constraint.isNullOrEmpty()) {
            filteredList.addAll(originalList)
        } else {
            val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

            originalList.forEach {
                if ((it.symbol + " - " + it.name).toLowerCase().contains(filterPattern)) {
                    filteredList.add(it)
                }
            }
        }
        results.values = filteredList
        results.count = filteredList.size
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
        adapter.filteredCurrency.clear()

        @Suppress("UNCHECKED_CAST")
        adapter.filteredCurrency.addAll(results.values as List<Coin>)

        adapter.notifyDataSetChanged()
    }
}