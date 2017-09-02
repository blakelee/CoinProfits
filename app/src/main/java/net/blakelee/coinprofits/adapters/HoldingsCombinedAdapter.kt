package net.blakelee.coinprofits.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.coin_item_main.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.BaseAdapter
import net.blakelee.coinprofits.base.BaseViewHolder
import net.blakelee.coinprofits.databinding.CoinItemMainBinding
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.HoldingsCombined
import net.cachapa.expandablelayout.ExpandableLayout

class HoldingsCombinedAdapter(val recyclerView: RecyclerView, val picasso: Picasso) : BaseAdapter<HoldingsCombined, HoldingsCombinedAdapter.HoldingsCombinedViewHolder>() {

    private val UNSELECTED = -1
    private var selectedItem: Int = UNSELECTED

    val longClick: PublishSubject<HoldingsCombined> = PublishSubject.create()

    override fun getItemViewId(): Int = R.layout.coin_item_main

    override fun instantiateViewHolder(view: View?): HoldingsCombinedViewHolder = HoldingsCombinedViewHolder(view)

    inner class HoldingsCombinedViewHolder(view: View?) : BaseViewHolder<HoldingsCombined>(view) {
        private val binding: CoinItemMainBinding = CoinItemMainBinding.bind(itemView)
        private val expandButton: RelativeLayout = itemView.item_top
        private val expandableLayout: ExpandableLayout = itemView.coin_item_expand
        private val coin_icon: ImageView = itemView.coin_icon

        override fun onBind(item: HoldingsCombined) {
            expandButton.isSelected = false
            expandableLayout.collapse(false)

            binding.holdingsCombined = item

            picasso.load(AppModule.IMAGE_URL + item.id+ ".png").into(coin_icon)

            if (!item.watchOnly()) {
                expandButton.setOnClickListener {
                    val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as HoldingsCombinedViewHolder?
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
                longClick.onNext(item)
                true
            }

            expandableLayout.setOnExpansionUpdateListener { _, _ ->
                recyclerView.smoothScrollToPosition(adapterPosition)
            }
        }
    }
}
