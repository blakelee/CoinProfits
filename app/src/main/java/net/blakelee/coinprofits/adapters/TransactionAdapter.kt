package net.blakelee.coinprofits.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.BaseViewHolder
import net.blakelee.coinprofits.databinding.ItemAddTransactionBinding
import net.blakelee.coinprofits.models.Transaction
import net.blakelee.coinprofits.tools.decimalFormat
import net.cachapa.expandablelayout.ExpandableLayout

class TransactionAdapter(val context: Context, val recyclerView: RecyclerView) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    var dataSource: MutableList<Transaction> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var viewClick: PublishSubject<View> = PublishSubject.create()

    fun addItem(transaction: Transaction) {
        dataSource.add(transaction)
        notifyItemInserted(itemCount)
    }

    fun removeItem(position: Int) {
        dataSource.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAll() {
        val size = itemCount
        dataSource.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun validate(): Boolean {
        var result = true
        dataSource.forEachIndexed { index, transaction ->
            val holder = recyclerView.findViewHolderForAdapterPosition(index) as TransactionViewHolder?
            holder?.let {

                if (it.publicKey.text.isNullOrEmpty() && it.amount.text.isNullOrEmpty()) {
                    it.amount.error = "You must either set the amount or add a public key"
                    result = false
                }
                if (it.price.text.isNullOrEmpty()) {
                    it.price.error = "You must set a buy-in price"
                    result = false
                }

                if (it.publicKey.text.isNotEmpty())
                    transaction.publicKey = it.publicKey.text.toString()

                if (it.amount.text.isNotEmpty())
                    transaction.amount = it.amount.text.toString().toDouble()

                if (it.price.text.isNotEmpty())
                    transaction.price = it.price.text.toString().toDouble()
            }
        }

        return result
    }

    private fun getItemViewId(): Int = R.layout.item_add_transaction

    fun instantiateViewHolder(view: View?) = TransactionViewHolder(view!!)

    override fun getItemCount() = dataSource.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemViewId(), parent, false)
        return instantiateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun getItem(position: Int) = dataSource[position]

    inner class TransactionViewHolder(val view: View) : BaseViewHolder<Transaction>(view) {
        private val binding: ItemAddTransactionBinding = ItemAddTransactionBinding.bind(view)
        private val dropdown: ImageView = view.findViewById(R.id.holdings_dropdown)
        private val details: ExpandableLayout = view.findViewById(R.id.transaction_details)
        private val close: ImageView = view.findViewById(R.id.holdings_close)
        private val check: Button = view.findViewById(R.id.transaction_check)
        val price: TextView = view.findViewById(R.id.transaction_price)
        val amount: TextView = view.findViewById(R.id.transaction_amount)
        val publicKey: TextView = view.findViewById(R.id.publicKey)
        val total: TextView = view.findViewById(R.id.holdings_total)

        override fun onBind(item: Transaction) {
            binding.transaction = item
            dropdown.setOnClickListener {
                details.toggle()
            }
            close.setOnClickListener {
                LovelyStandardDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_info)
                        .setTitle("Delete transaction?")
                        .setMessage("Are you sure that you want to remove this transaction?")
                        .setPositiveButton(android.R.string.ok,  { removeItem(adapterPosition) })
                        .setNegativeButton(android.R.string.no, null)
                        .show()
            }
            check.setOnClickListener {
                viewClick.onNext(view)
            }

            RxTextView.afterTextChangeEvents(amount)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (price.text.isNotEmpty() && amount.text.isNotEmpty()) {
                            total.text = String.format("$%s", decimalFormat(price.text.toString().toDouble() * amount.text.toString().toDouble()))
                        }
                    }

            RxTextView.afterTextChangeEvents(price)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (price.text.isNotEmpty() && amount.text.isNotEmpty()) {
                            total.text = String.format("$%s", decimalFormat(price.text.toString().toDouble() * amount.text.toString().toDouble()))
                        }
                    }

            RxTextView.afterTextChangeEvents(publicKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (!amount.isEnabled) {
                            amount.isEnabled = true
                            amount.text = "0.0"
                        }
                        publicKey.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    }
        }
    }
}