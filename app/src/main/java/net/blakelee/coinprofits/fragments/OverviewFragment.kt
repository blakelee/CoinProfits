package net.blakelee.coinprofits.fragments

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.*
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
import net.blakelee.coinprofits.adapters.PeriodAdapter
import net.blakelee.coinprofits.base.SwipeViewPager
import net.blakelee.coinprofits.models.HoldingsOverview
import net.blakelee.coinprofits.repository.ChartRepository
import net.blakelee.coinprofits.repository.HoldingsRepository
import net.blakelee.coinprofits.repository.PreferencesRepository
import net.blakelee.coinprofits.tools.*
import net.blakelee.cryptochart.CryptoChart
import net.blakelee.cryptochart.decimalFormat
import net.cachapa.expandablelayout.ExpandableLayout
import retrofit2.HttpException
import java.net.UnknownHostException
import java.text.NumberFormat
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
    private var expanded: Boolean = false

    private lateinit var pager: SwipeViewPager
    private lateinit var spinner: Spinner
    private lateinit var adapter: ArrayAdapter<HoldingsOverview>
    private lateinit var slideInTop: Animation
    private lateinit var slideOutTop: Animation
    private lateinit var rotateDown: Animation
    private lateinit var rotateUp: Animation
    private lateinit var increase: Drawable
    private lateinit var decrease: Drawable

    private val portfolioValue by lazy { view!!.findViewById<TextView>(R.id.portfolio_value) }
    private val portfolioChange24h by lazy { view!!.findViewById<TextView>(R.id.portfolio_change_24h) }
    private val portfolioChange24hImg by lazy { view!!.findViewById<ImageView>(R.id.portfolio_change_24h_img) }
    private val portfolioMargins by lazy { view!!.findViewById<TextView>(R.id.portfolio_margins) }
    private val portfolioMarginsImg by lazy { view!!.findViewById<ImageView>(R.id.portfolio_margins_img) }
    private val portfolioChange7d by lazy { view!!.findViewById<TextView>(R.id.portfolio_change_7d) }
    private val portfoliochange7dImg by lazy { view!!.findViewById<ImageView>(R.id.portfolio_change_7d_img) }
    private val portfolioMarginsFiat by lazy { view!!.findViewById<TextView>(R.id.portfolio_margins_fiat) }
    private val portfolioMarginsFiatImg by lazy { view!!.findViewById<ImageView>(R.id.portfolio_margins_fiat_img) }
    private val portfolioBuyin by lazy { view!!.findViewById<TextView>(R.id.portfolio_buyin) }
    private val portfolioToggle by lazy { view!!.findViewById<ImageView>(R.id.portfolio_toggle) }
    private val portfolioExpandable by lazy { view!!.findViewById<ExpandableLayout>(R.id.portfolio_expandable) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_overview, container, false)

    //Start Observables
    override fun onResume() {
        super.onResume()

        (PeriodRecycler.adapter as PeriodAdapter).click
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe {
                    updatePeriod(when (it) {
                        0 -> Period.DAY
                        1 -> Period.WEEK
                        2 -> Period.MONTH
                        3 -> Period.QUARTER
                        4 -> Period.SEMESTER
                        5 -> Period.YEAR
                        6 -> Period.ALL
                        else -> period
                    })
                }

        (PeriodRecycler.adapter as PeriodAdapter).onResume()

        hRepo.getHoldingsOverview()
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe { items ->
                    adapter.clear()
                    adapter.addAll(items)
                }

        hRepo.getHoldingsCombined()
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe { items ->
                    val value = items.sumByDouble { it.price * it.transaction.sumByDouble { it.amount } }
                    var percent24h = 0.0
                    var percent7d = 0.0

                    val buyIn = items.sumByDouble { it.transaction.sumByDouble { it.amount * it.price } }
                    val margins = ((value / buyIn) * 100) - 100

                    items.forEach { item ->
                        if (item.transaction.isNotEmpty()) {
                            item.percent_change_24h?.let {
                                percent24h += ((item.transaction.sumByDouble { it.amount } * item.price) / value) * it
                            }

                            item.percent_change_7d?.let {
                                percent7d += ((item.transaction.sumByDouble { it.amount } * item.price) / value) * it
                            }
                        }
                    }

                    val format = NumberFormat.getInstance()
                    format.maximumFractionDigits = 2
                    format.minimumFractionDigits = 2

                    when {
                        margins < 0 -> portfolioMarginsImg.setImageDrawable(decrease)
                        margins > 0 -> portfolioMarginsImg.setImageDrawable(increase)
                        else -> portfolioMarginsImg.setImageDrawable(null)
                    }

                    when {
                        percent24h < 0 -> portfolioChange24hImg.setImageDrawable(decrease)
                        percent24h > 0 -> portfolioChange24hImg.setImageDrawable(increase)
                        else -> portfolioChange24hImg.setImageDrawable(null)
                    }

                    when {
                        percent7d < 0 -> portfoliochange7dImg.setImageDrawable(decrease)
                        percent7d > 0 -> portfoliochange7dImg.setImageDrawable(increase)
                        else -> portfoliochange7dImg.setImageDrawable(null)
                    }

                    val marginsFiat = value - buyIn

                    when {
                        marginsFiat < 0 -> portfolioMarginsFiatImg.setImageDrawable(decrease)
                        marginsFiat > 0 -> portfolioMarginsFiatImg.setImageDrawable(increase)
                        else -> portfolioMarginsFiatImg.setImageDrawable(null)
                    }

                    portfolioValue.text = String.format("$%s", decimalFormat(value))
                    portfolioChange24h.text = String.format("%s%%", format.format(abs(percent24h)))
                    portfolioMargins.text = String.format("%s%%", format.format(abs(margins)))
                    portfolioChange7d.text = String.format("%s%%", format.format(abs(percent7d)))
                    portfolioMarginsFiat.text = String.format("$%s", decimalFormat(marginsFiat))
                    portfolioBuyin.text = String.format("$%s", decimalFormat(buyIn))

                }
    }

    private fun getChartData(id: String) {
        val last = Date().time
        val first = last - TimeFormatter(period)

        compositeDisposable.clear()

        cRepo.getChartData(id, first, last)
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                .subscribe ({
                    compositeDisposable.clear()
                    it.priceUsd?.let {

                        if (it.isNotEmpty()) {
                            since.text = SinceFormatter(period, it.first()[0])
                            price.text = String.format("$%s", it.last()[1].decimalFormat)
                            onValueSelected(Entry(it.last()[0], it.last()[1]))

                            if (it.first()[1] < it.last()[1]) {
                                img_change.setImageDrawable(increase)
                                change.text = String.format("$%s", (it.last()[1] - it.first()[1]).decimalFormat)
                            } else {
                                img_change.setImageDrawable(decrease)
                                change.text = String.format("$%s", abs(it.last()[1] - it.first()[1]).decimalFormat)
                            }

                        }

                        cryptoChart.setData(it)
                    }
                }, { error ->
                    Toast.makeText(context,
                    when (error) {
                        is HttpException -> {"Too many api requests. Only 10 per minute allowed"}
                        is UnknownHostException -> "Internet unavailable. Try again later"
                        else -> error.message
                    }
                            , Toast.LENGTH_SHORT).show()
                }).addTo(compositeDisposable)
    }

    private fun updatePeriod(period: Period) {
        if (this.period != period) {
            this.period = period
            getChartData(((spinner.selectedItem) as HoldingsOverview).id)
            cryptoChart.setFormatter(DateFormatter(period))
            cryptoChart.setLabelCount(LabelCount(period))
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item: HoldingsOverview = adapter.getItem(position)

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
        setHasOptionsMenu(true)
        PeriodRecycler.adapter = PeriodAdapter()
        pager = activity.findViewById(R.id.pager)
        spinner = activity.findViewById(R.id.chart_items)
        adapter = ArrayAdapter(context, R.layout.spinner_row)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        slideInTop = AnimationUtils.loadAnimation(context, R.anim.in_top)
        slideOutTop = AnimationUtils.loadAnimation(context, R.anim.out_top)
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_180_down)
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_180_up)
        increase = ContextCompat.getDrawable(context, R.drawable.ic_increase)
        decrease = ContextCompat.getDrawable(context, R.drawable.ic_decrease)
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

        portfolioToggle.setOnClickListener {
            if (!expanded) {
                portfolioExpandable.expand()
                portfolioToggle.startAnimation(rotateDown)
            }
            else {
                portfolioExpandable.collapse()
                portfolioToggle.startAnimation(rotateUp)
            }

            expanded = expanded.xor(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_refresh -> {
                hRepo.refreshHoldings("usd")
                        .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE)
                        .subscribe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_overview, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun getLifecycle(): LifecycleRegistry = registry

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}