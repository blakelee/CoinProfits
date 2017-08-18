package net.blakelee.coinprofits.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.Coin
import javax.inject.Inject

class AutoCompleteCurrencyAdapter @Inject constructor(context: Context, private val picasso: Picasso) : ArrayAdapter<Coin>(context, 0) {

    var originalList: List<Coin> = emptyList()

    private val filter = object : Filter() {
        override fun convertResultToString(resultValue: Any?) = (resultValue as Coin).toString()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = Filter.FilterResults()

            constraint?.let {
                val suggestions = ArrayList<Coin>()
                originalList.forEach {
                    if (it.toString().toLowerCase().contains(constraint.toString().toLowerCase()))
                        suggestions.add(it)
                }

                results.values = suggestions
                results.count = suggestions.size
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            if (results != null && results.count > 0)
                @Suppress("UNCHECKED_CAST")
                addAll(results.values as ArrayList<Coin>)
            else
                addAll(originalList)

            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item from filtered list.
        val coin = getItem(position)

        val inflater = LayoutInflater.from(context)
        var view: View? = convertView

        if (view == null)
            view = inflater.inflate(R.layout.dialog_item_coin, parent, false)

        val text: TextView = view!!.findViewById(R.id.coin_text)
        val icon: ImageView = view.findViewById(R.id.coin_icon)
        text.text = coin.toString()

        picasso.load(AppModule.IMAGE_URL + coin.id + ".png").into(icon)

        convertView?.setOnTouchListener { v, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
            }
            false
        }

        return view
    }

    override fun getFilter(): Filter = filter
}