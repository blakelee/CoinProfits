package net.blakelee.coinprofits.pricechart

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_overview.*
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.SwipeViewPager
import net.blakelee.coinprofits.tools.decimalFormat
import net.blakelee.coinprofits.tools.px
import net.blakelee.coinprofits.tools.textDim
import java.text.SimpleDateFormat
import java.util.*

class PriceChart(private val chart: LineChart): OnChartValueSelectedListener, OnChartGestureListener {

    //Alt View
    var chart_price: TextView? = null
    var chart_date: TextView? = null

    //Split View
    var price: TextView? = null
    var currency: TextView? = null
    var change: TextView? = null
    var since:TextView? = null
    var img_change: ImageView? = null

    var pager: SwipeViewPager? = null
    var prices_container: RelativeLayout? = null
    var slideIn: Animation? = null
    var slideOut: Animation? = null
    var slideInTop: Animation? = null
    var slideOutTop: Animation? = null
    var price_high: TextView? = null
    var price_mid: TextView? = null
    var price_low: TextView? = null
    var splitView: View? = null         //This is the standard non-clicked view
    var altView: View? = null           //This view gets shown when chart is dragged

    val current: Locale = Locale.getDefault()
    var period: Period = Period.DAY
    var max: Float = 0f
    var maxDate: Float = 0f
    var color: Int = ContextCompat.getColor(chart.context, R.color.colorLine)

    init {
        styleChart()
        styleYaxis()
        styleXAxis()
        setPrices()
    }

    fun addData(data: List<List<Float>>) {
        max = data.last()[1]
        maxDate = data.last()[0]
        val first = data.first()[1]
        val firstDate = data.first()[0]

        val change = max - first

        if (change < 0)
            img_change?.background = ContextCompat.getDrawable(chart.context, R.drawable.ic_decrease)
        else
            img_change?.background = ContextCompat.getDrawable(chart.context, R.drawable.ic_increase)


        price?.text = String.format("$%s",decimalFormat(max.toDouble()))
        this.change?.text = String.format("$%s",decimalFormat(Math.abs(max.toDouble() - first.toDouble())))
        chart_date?.text = SimpleDateFormat("M/d/yy hh:mm a", current).format(maxDate)
        since?.text = SinceFormatter(period, firstDate)


        val dataSet = mutableListOf<Entry>()
        data.forEach {
            dataSet.add(Entry(it[0], it[1]))
        }
        chart.xAxis.valueFormatter = DateFormatter(period)
        val lineDataSet = LineDataSet(dataSet, "")
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.color = Color.WHITE
        lineDataSet.fillDrawable = ContextCompat.getDrawable(chart.context, R.drawable.fade_red)
        lineDataSet.setDrawFilled(true)

        val iLineData = mutableListOf<ILineDataSet>()
        iLineData.add(lineDataSet)

        val lineData = LineData(iLineData)
        val label = LabelCount()

        chart.data = lineData
        chart.xAxis.setLabelCount(label.first, label.second)
        val min = chart.yChartMin
        val max = chart.yChartMax

        val step = (max - min) / 4

        val y1 = min + step
        val y2 = min + 2 * step
        val y3 = min + 3 * step

        setPrices(listOf(y1, y2, y3))
        chart.invalidate()
    }

    private fun setPrices(prices: List<Float>) {
        var longest: Float = 0f

        prices.forEach {
            val cur = decimalFormat(longest.toDouble())
            val new = decimalFormat(it.toDouble())
            if (textDim(cur, chart.context, 14f) < textDim(new, chart.context, 14f))
                longest = it
        }

        //TODO: Format prices so that they have the same decimal places for each one
        price_low?.text = String.format("$%s",decimalFormat(prices[0].toDouble()))
        price_mid?.text = String.format("$%s",decimalFormat(prices[1].toDouble()))
        price_high?.text = String.format("$%s",decimalFormat(prices[2].toDouble()))


        val longestText = decimalFormat(longest.toDouble())

        val textPaint = Paint()
        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 14f
        val r = chart.context.resources

        //This seems to get the perfect dimensions
        val bounds = Rect()
        textPaint.getTextBounds(longestText, 0, longestText.length, bounds)
        val mWidth: Float = bounds.width().toFloat()
        val mTextWidth: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mWidth, r.displayMetrics)

        val width: Float = textPaint.measureText("$" + longestText)
        val textWidth: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.displayMetrics)
        val margin: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.toFloat(), r.displayMetrics)

        chart.axisLeft.enableGridDashedLine(5000.px, textWidth + margin, 5000.px)

    }

    fun setPrices() {
        price_low?.text = ""
        price_mid?.text = ""
        price_high?.text = ""
    }

    private fun showPrices() {
        prices_container?.startAnimation(slideIn)
        prices_container?.visibility = View.VISIBLE
        altView?.visibility = View.GONE
        altView?.startAnimation(slideOutTop)
        splitView?.visibility = View.VISIBLE
        splitView?.startAnimation(slideInTop)
    }

    private fun hidePrices() {
        prices_container?.visibility = View.GONE
        prices_container?.startAnimation(slideOut)
        altView?.visibility = View.VISIBLE
        altView?.startAnimation(slideInTop)
        splitView?.visibility = View.GONE
        splitView?.startAnimation(slideOutTop)
    }

    private fun LabelCount(): Pair<Int, Boolean> {
        return when (period) {
            Period.DAY -> Pair(7, true)
            Period.WEEK,
            Period.MONTH -> Pair(8, true)
            Period.QUARTER,
            Period.SEMESTER -> Pair(7, true)
            Period.YEAR -> Pair(13, true)
            Period.ALL -> Pair(7, false)
        }
    }

    private fun styleChart() {
        chart.extraBottomOffset = 2f
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.isHighlightPerDragEnabled = true //Crosshair only when dragging
        chart.isHighlightPerTapEnabled = false //Don't persist crosshair
        chart.setOnChartValueSelectedListener(this) //change price textview
        chart.onChartGestureListener = this //Check whether finger down/up
        chart.isDragEnabled = true
        chart.setPinchZoom(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setScaleEnabled(false)
        chart.setDrawBorders(false)
        chart.setGridBackgroundColor(Color.BLUE)
        chart.minOffset = 0f
        chart.axisRight.isEnabled = false
        chart.invalidate()
    }

    private fun styleYaxis() {
        val leftAxis: YAxis = chart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.xOffset = 15.0f
        leftAxis.valueFormatter = IAxisValueFormatter { _, _ ->  "" }
        leftAxis.axisLineColor = color
        leftAxis.axisLineWidth = 1.0f
        leftAxis.gridColor = Color.argb(75, 255, 255, 255)
        leftAxis.gridLineWidth = 1.0f
        leftAxis.spaceTop = 20.0f   //Translates the top lower so you can see high values better
        leftAxis.spaceBottom = 20.0f //Translates the bottoms higher so you can see the floor better
        leftAxis.textColor = Color.WHITE
        leftAxis.textSize = 12.0f
        leftAxis.setLabelCount(5, true)
        //leftAxis.enableGridDashedLine(5000.px, 80.px, 5000.px) //Not actually dashed. Removes left side
        leftAxis.setCenterAxisLabels(false)
    }

    private fun styleXAxis(){//, dates: List<Date>) {
        val xAxis = chart.xAxis
        xAxis.setDrawLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.yOffset = 2f
        xAxis.axisLineColor = color
        xAxis.textColor = color
        xAxis.axisLineWidth = 1f
        xAxis.textSize = 12f
        xAxis.setLabelCount(8, true)
    }

    //Set left axis labels view, set price indicator current value
    private fun handleGestureEnd(me: MotionEvent?) {
        if (me?.action == MotionEvent.ACTION_DOWN || me?.action == MotionEvent.ACTION_UP || me?.action == MotionEvent.ACTION_CANCEL) {
            val isDown = me.action != MotionEvent.ACTION_DOWN
            chart.axisLeft.isEnabled = isDown
        }
    }

    //Enable price view
    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        chart.highlightValues(null)
        pager?.setSwipeable(true)
        handleGestureEnd(me)

        chart_date?.text = SimpleDateFormat("M/d/yy hh:mm a", current).format(maxDate)
        chart_price?.text = String.format("$%s",decimalFormat(max.toDouble()))
        showPrices()
    }

    //Disable price view
    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        pager?.setSwipeable(false)
        handleGestureEnd(me)
        hidePrices()
    }

    //Update textview showing price
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        e?.let {
            chart_price?.text = String.format("$%s", decimalFormat(e.y.toDouble()))
            chart_date?.text = SimpleDateFormat("M/d/yy hh:mm a", current).format(e.x)
        }
    }

    override fun onChartLongPressed(me: MotionEvent?) {}
    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
    override fun onChartSingleTapped(me: MotionEvent?) {}
    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
    override fun onNothingSelected() {}
    override fun onChartDoubleTapped(me: MotionEvent?) {}
    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}
}