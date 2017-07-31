package net.blakelee.coinprofits.pricechart

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
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
import net.blakelee.coinprofits.R
import net.blakelee.coinprofits.base.SwipeViewPager
import net.blakelee.coinprofits.tools.decimalFormat
import net.blakelee.coinprofits.tools.px
import java.text.SimpleDateFormat
import java.util.*

class PriceChart(private val chart: LineChart): OnChartValueSelectedListener, OnChartGestureListener {

    var chart_price: TextView? = null
    var chart_date: TextView? = null
    var pager: SwipeViewPager? = null
    var splitView: View? = null         //This is the standard non-clicked view
    var altView: View? = null           //This view gets shown when chart is dragged
    var yValues: View? = null           //This view shows the y values when chart is not clicked

    val current: Locale = Locale.getDefault()
    var period: Period = Period.DAY
    var max: Float = 0f
    var maxDate: Float = 0f
    var color: Int = ContextCompat.getColor(chart.context, R.color.colorLine)

    init {
        styleChart()
        styleYaxis()
        styleXAxis()
    }

    fun addData(data: List<List<Float>>) {
        max = data.last()[1]
        maxDate = data.last()[0]

        chart_price?.text = decimalFormat(max.toDouble())
        chart_date?.text = SimpleDateFormat("M/d/yy hh:mm a", current).format(maxDate)

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
        chart.invalidate()
    }

    private fun LabelCount(): Pair<Int, Boolean> {
        return when (period) {
            Period.HOUR -> Pair(6, true)
            Period.DAY -> Pair(7, true)
            Period.WEEK -> Pair(8, true)
            Period.MONTH -> Pair(8, true)
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
        leftAxis.gridColor = Color.argb(35, 255, 255, 255)
        leftAxis.gridLineWidth = 1.0f
        leftAxis.spaceTop = 20.0f   //Translates the top lower so you can see high values better
        leftAxis.spaceBottom = 20.0f //Translates the bottoms higher so you can see the floor better
        leftAxis.textColor = Color.WHITE
        leftAxis.textSize = 12.0f
        leftAxis.setLabelCount(5, true)
        leftAxis.enableGridDashedLine(5000.px, 80.px, 5000.px) //Not actually dashed. Removes left side
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
        chart_price?.text = decimalFormat(max.toDouble())
    }

    //Disable price view
    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        pager?.setSwipeable(false)
        handleGestureEnd(me)
    }

    //Update textview showing price
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        e?.let {
            chart_price?.text = decimalFormat(e.y.toDouble())
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