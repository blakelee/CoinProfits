package net.blakelee.coinprofits.adapters

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_period.view.*
import net.blakelee.coinprofits.R

class PeriodAdapter : RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder>() {

    private val items: List<String> = listOf("Day", "Week", "Month", "Quarter", "Semester", "Year", "All")
    private var recyclerView: RecyclerView? = null
    private var selected = items.indexOf("Day")
    private var leftMargin = -1
    private var rightMargin = -1
    val click: PublishSubject<Int> = PublishSubject.create()

    fun onResume() {
        click.onNext(selected)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PeriodViewHolder, position: Int) {
        holder.itemView.text.text = items[position]
        holder.itemView.text.isPressed = selected == position
        holder.itemView.text.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val scroll = (holder.itemView.left - holder.itemView.context.resources.displayMetrics.widthPixels / 2) + (holder.itemView.width / 2)
                recyclerView?.smoothScrollBy(scroll, 0, OvershootInterpolator(0.5f))
                selected = position
                click.onNext(selected)
            }
            true
        }

        //This is super inefficient, however, notifyItemChanged(position) messes with the padding for some reason
        //Changing it by getting the viewholder and setting the previous doesn't work for items outside of the view
        click.subscribe {
            holder.itemView.text.isPressed = it == position
        }

        if (position == 0) {
            if (leftMargin < 0)
                leftMargin = calculateMargin(holder.itemView)

            setLeftMargin(holder.itemView)
        }
        else if (position == itemCount - 1) {
            if (rightMargin < 0)
                rightMargin = calculateMargin(holder.itemView)

            setRightMargin(holder.itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodViewHolder =
            PeriodViewHolder(View.inflate(parent.context, R.layout.item_period, null))

    inner class PeriodViewHolder(view: View?) : RecyclerView.ViewHolder(view)

    private fun setLeftMargin(view: View) {
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.leftMargin = leftMargin
        view.layoutParams = lp
    }

    private fun setRightMargin(view: View) {
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.rightMargin = rightMargin
        view.layoutParams = lp
    }

    private fun calculateMargin(view: View): Int {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return view.context.resources.displayMetrics.widthPixels / 2 - view.measuredWidth / 2
    }
}