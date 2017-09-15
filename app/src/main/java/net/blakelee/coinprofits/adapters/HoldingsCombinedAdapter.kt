package net.blakelee.coinprofits.adapters

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.coin_item_main.view.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.*
import net.blakelee.coinprofits.databinding.CoinItemMainBinding
import net.blakelee.coinprofits.di.AppModule
import net.blakelee.coinprofits.models.HoldingsCombined
import net.cachapa.expandablelayout.ExpandableLayout
import java.util.*

class HoldingsCombinedAdapter(val recyclerView: RecyclerView, val picasso: Picasso) : BaseAdapter<HoldingsCombined, HoldingsCombinedAdapter.HoldingsCombinedViewHolder>(), ItemTouchHelperAdapter {

    private val UNSELECTED = -1
    private var selectedItem: Int = UNSELECTED

    val longClick: PublishSubject<HoldingsCombined> = PublishSubject.create()
    var onStartDragListener: OnStartDragListener? = null

    override var editMode: Boolean = false
    set(value) {
        field = value
        rxEditMode.onNext(field)

        if (field)
            selectedItem = UNSELECTED
    }

    private val rxEditMode: PublishSubject<Boolean> = PublishSubject.create()

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(dataSource, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemViewId(): Int = R.layout.coin_item_main

    override fun instantiateViewHolder(view: View?): HoldingsCombinedViewHolder = HoldingsCombinedViewHolder(view)

    inner class HoldingsCombinedViewHolder(view: View?) : BaseViewHolder<HoldingsCombined>(view), ItemTouchHelperViewHolder {
        private val binding: CoinItemMainBinding = CoinItemMainBinding.bind(itemView)
        private val expandButton: RelativeLayout = itemView.item_top
        private val expandableLayout: ExpandableLayout = itemView.coin_item_expand
        private val orderButton: ImageView = itemView.item_reorder
        private val coin_icon: ImageView = itemView.coin_icon

        override fun onBind(item: HoldingsCombined) {
            expandButton.isSelected = false
            expandableLayout.collapse(false)

            binding.holdingsCombined = item
            binding.editMode = editMode

            rxEditMode.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe {
                        expandableLayout.collapse()
                        binding.editMode = it
                    }

            picasso.load(AppModule.IMAGE_URL + item.id+ ".png").into(coin_icon)

            orderButton.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN && editMode)
                    onStartDragListener?.startDrag(this)

                true
            }

            if (!item.watchOnly()) {
                expandButton.setOnClickListener {
                    if (!editMode) {
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
            }

            expandButton.setOnLongClickListener {
                longClick.onNext(item)
                true
            }

            expandableLayout.setOnExpansionUpdateListener { _, _ ->
                recyclerView.smoothScrollToPosition(adapterPosition)
            }
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.cardColor))
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.cardSelected))
        }
    }
}
