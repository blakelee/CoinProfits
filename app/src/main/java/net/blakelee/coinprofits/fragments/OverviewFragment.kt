package net.blakelee.coinprofits.fragments

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.github.mikephil.charting.data.Entry
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_overview.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.SwipeViewPager
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.repository.ChartRepository
import net.blakelee.coinprofits.repository.HoldingsRepository
import net.blakelee.coinprofits.repository.PreferencesRepository
import net.blakelee.coinprofits.tools.*
import net.blakelee.cryptochart.CryptoChart
import net.blakelee.cryptochart.decimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class OverviewFragment : Fragment(), LifecycleRegistryOwner, AdapterView.OnItemSelectedListener, CryptoChart.TouchListener {

    @Inject lateinit var cRepo: ChartRepository
    @Inject lateinit var hRepo: HoldingsRepository
    @Inject lateinit var prefs: PreferencesRepository
    private val registry = LifecycleRegistry(this)
    private var compositeDisposable = CompositeDisposable()
    private var locale = Locale.getDefault()
    private var period = Period.DAY

    private lateinit var pager: SwipeViewPager
    private lateinit var spinner: Spinner
    private lateinit var adapter: ArrayAdapter<Holdings>

    private lateinit var slideInTop: Animation
    private lateinit var slideOutTop: Animation

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_overview, container, false)

    //Start Observables
    override fun onResume() {
        super.onResume()

        hRepo.getHoldings(prefs.ordered)
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe {
                    adapter.clear()
                    adapter.addAll(it)
                }
    }

    private fun getChartData(id: String) {
        val last = Date().time
        val first = last - TimeFormatter(period)

        compositeDisposable.clear()

        cRepo.getChartData(id, first, last)
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe {
                    it.priceUsd?.let {

                        if (it.isNotEmpty()) {
                            since.text = SinceFormatter(period, it.first()[0])
                            price.text = String.format("$%s", it.last()[1].decimalFormat)
                            onValueSelected(Entry(it.last()[0], it.last()[1]))

                            if (it.first()[1] < it.last()[1]) {
                                img_change.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_increase))
                                change.text = String.format("$%s", (it.last()[1] - it.first()[1]).decimalFormat)
                            } else {
                                img_change.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_decrease))
                                change.text = String.format("$%s", abs(it.last()[1] - it.first()[1]).decimalFormat)
                            }

                        }

                        cryptoChart.setData(it)
                    }
                }.addTo(compositeDisposable)
    }

    private fun updatePeriod(period: Period) {
        if (this.period != period) {
            val prev: TextView = time_holder.getChildAt(this.period.value) as TextView
            prev.setBackgroundColor(ContextCompat.getColor(context, R.color.background))
            prev.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))


            this.period = period
            getChartData(((spinner.selectedItem) as Holdings).id)
            cryptoChart.setFormatter(DateFormatter(period))
            cryptoChart.setLabelCount(LabelCount(period))
            setPeriodColors(this.period)
        }
    }

    private fun setPeriodColors(period: Period) {
        val selected: TextView = time_holder.getChildAt(period.value) as TextView
        selected.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedBackground))
        selected.setTextColor(ContextCompat.getColor(context, R.color.textHighlighted))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item: Holdings = adapter.getItem(position)

        val textPaint = Paint()
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 14f
        var width = textPaint.measureText(item.toString())

        width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.resources.displayMetrics)

        val paddingWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, context.resources.displayMetrics)

        spinner.layoutParams.width = (width + paddingWidth).toInt()
        spinner.requestLayout()
        currency?.text = String.format("%s price", item.name)
        getChartData(item.id)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onChartGestureEnd() {
        pager.setSwipeable(true)
        prices_details_specific_container.visibility = View.GONE
        prices_details_specific_container.startAnimation(slideOutTop)
        price_details_single_container.visibility = View.VISIBLE
        price_details_single_container.startAnimation(slideInTop)
    }

    override fun onChartGestureStart() {
        pager.setSwipeable(false)
        prices_details_specific_container?.visibility = View.VISIBLE
        prices_details_specific_container?.startAnimation(slideInTop)
        price_details_single_container?.visibility = View.GONE
        price_details_single_container?.startAnimation(slideOutTop)
    }

    override fun onValueSelected(entry: Entry) {
        chart_price?.text = String.format("$%s", entry.y.decimalFormat)
        chart_date?.text = SimpleDateFormat("M/d/yy hh:mm a", locale).format(entry.x)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pager = activity.findViewById(R.id.pager)
        spinner = activity.findViewById(R.id.chart_items)
        adapter = ArrayAdapter(context, R.layout.spinner_row)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        slideInTop = AnimationUtils.loadAnimation(context, R.anim.in_top)
        slideOutTop = AnimationUtils.loadAnimation(context, R.anim.out_top)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        cryptoChart.listener = this
        cryptoChart.fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_red)
        cryptoChart.dataLineColor = Color.WHITE
        cryptoChart.chartLineColor = ContextCompat.getColor(context, R.color.colorLine)
        cryptoChart.textColor = ContextCompat.getColor(context, R.color.textPrimary)
        cryptoChart.setFormatter(DateFormatter(period))
        cryptoChart.gridLineColor = Color.argb(75, 255, 255, 255)

        val start = ContextCompat.getColor(context, R.color.pricesStart)
        val end = ContextCompat.getColor(context, R.color.pricesEnd)
        cryptoChart.setFadeColors(start, end)

        setPeriodColors(this.period)

        day.setOnClickListener { updatePeriod(Period.DAY) }
        week.setOnClickListener { updatePeriod(Period.WEEK) }
        month.setOnClickListener { updatePeriod(Period.MONTH) }
        quarter.setOnClickListener { updatePeriod(Period.QUARTER) }
        semester.setOnClickListener { updatePeriod(Period.SEMESTER) }
        year.setOnClickListener { updatePeriod(Period.YEAR) }
        all.setOnClickListener { updatePeriod(Period.ALL) }
    }

    override fun getLifecycle(): LifecycleRegistry = registry

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}