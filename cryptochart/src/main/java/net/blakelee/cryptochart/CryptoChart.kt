package net.blakelee.cryptochart

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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


class CryptoChart : RelativeLayout, OnChartValueSelectedListener, OnChartGestureListener {

    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int): super(context, attr, defStyleAttr)

    @LayoutRes private val pricesLayout: Int = R.layout.crypto_chart

    private val view: View = LayoutInflater.from(context).inflate(pricesLayout, this, true)
    private val prices_container: RelativeLayout = findViewById(R.id.prices_container)
    private val price_high: TextView = findViewById(R.id.price_high)
    private val price_mid: TextView = findViewById(R.id.price_mid)
    private val price_low: TextView = findViewById(R.id.price_low)
    private val chart: LineChart = findViewById(R.id.stacked_area_chart)
    private val bottomLine: View = findViewById(R.id.bottom_line)
    private val fade: View = findViewById(R.id.fade)                           //The prices fade part
    private val prices_left: ConstraintLayout = findViewById(R.id.prices_left) //The view left of the fade

    private val metrics: DisplayMetrics = context.resources.displayMetrics
    private val lineLength: Float = 5000f
    private val margin: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, metrics) //8dp margin on left + 4dp margin at the end

    private var slideIn: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in)
    private var slideOut: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_out)

    var textColor: Int = Color.BLACK
    set(value) {
        field = value
        chart.xAxis.textColor = value
        price_high.setTextColor(value)
        price_mid.setTextColor(value)
        price_low.setTextColor(value)
    }


    var dataLineColor: Int = Color.WHITE

    var chartLineColor: Int = ContextCompat.getColor(context, R.color.colorLine)
    set(value) {
        field = value
        chart.xAxis.axisLineColor = value
        chart.axisLeft.axisLineColor = value
        bottomLine.setBackgroundColor(value)
    }

    var gridLineColor: Int = Color.argb(75, 0, 0, 0)
    set(value) {
        field = value
        chart.axisLeft.gridColor = value
    }

    var chartColor: Int = Color.BLACK
    var valueFormatter: IAxisValueFormatter? = null
    var fillDrawable: Drawable? = null
    var listener: TouchListener? = null

    interface TouchListener {
        fun onValueSelected(entry: Entry)
        fun onChartGestureStart()
        fun onChartGestureEnd()
    }

    init {
        styleChart()
        styleYAxis()
        styleXAxis()
        setPrices()
        textColor = textColor
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Suppress("deprecation")
    fun setFadeColors(start: Int, end: Int) {
        val colorArr = intArrayOf(start, end)
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colorArr)

        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            fade.setBackgroundDrawable(gradientDrawable)
        } else {
            fade.background = gradientDrawable
        }

        prices_left.setBackgroundColor(start)
    }

    fun setFormatter(formatter: IAxisValueFormatter) {
        chart.xAxis.valueFormatter = formatter
    }

    fun setLabelCount(pair: Pair<Int, Boolean>) {
        chart.xAxis.setLabelCount(pair.first, pair.second)
    }

    fun setData(data: List<List<Float>>) {

        if (data.isEmpty()) {
            setPrices()
            return
        }

        val dataSet = mutableListOf<Entry>()
        data.forEach { dataSet.add(Entry(it[0], it[1])) }

        val lineDataSet = LineDataSet(dataSet, "")
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.color = dataLineColor
        lineDataSet.fillDrawable = fillDrawable
        lineDataSet.setDrawFilled(true)

        val iLineData = mutableListOf<ILineDataSet>()
        iLineData.add(lineDataSet)

        val lineData = LineData(iLineData)

        chart.data = lineData
        val min = chart.yChartMin
        val max = chart.yChartMax

        val step = (max - min) / 4

        setPrices(listOf(min + step, min + 2 * step, min + 3 * step))
        chart.invalidate()
    }

    private fun setPrices(prices: List<Float>) {
        var width = 0f
        prices.forEach { width = maxOf(width, getWidth("$" + it.decimalFormat)) }
        price_low.text = String.format("$%s", prices[0].decimalFormat)
        price_mid.text = String.format("$%s", prices[1].decimalFormat)
        price_high.text = String.format("$%s", prices[2].decimalFormat)
        chart.axisLeft.enableGridDashedLine(lineLength, width + margin, lineLength)
    }

    private fun setPrices() {
        price_high.text = ""
        price_mid.text = ""
        price_low.text = ""
        chart.axisLeft.enableGridDashedLine(lineLength, 0f, lineLength)
    }

    private fun handleGestureEnd(me: MotionEvent?) {
        if (me?.action == MotionEvent.ACTION_DOWN || me?.action == MotionEvent.ACTION_UP || me?.action == MotionEvent.ACTION_CANCEL) {
            val isDown = me.action != MotionEvent.ACTION_DOWN
            chart.axisLeft.isEnabled = isDown
        }
    }

    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        prices_container.startAnimation(slideOut)
        prices_container.visibility = View.GONE
        chart.axisLeft.isEnabled = false
        listener?.onChartGestureStart()
    }
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        e?.let { listener?.onValueSelected(e) }
    }

    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        chart.highlightValues(null)
        handleGestureEnd(me)
        prices_container.startAnimation(slideIn)
        prices_container.visibility = View.VISIBLE
        chart.axisLeft.isEnabled = true
        listener?.onChartGestureEnd()
    }

    private fun styleChart() {
        chart.extraBottomOffset = 2f
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.minOffset = 0f
        chart.axisRight.isEnabled = false
        chart.axisRight.setDrawAxisLine(false)
        chart.onChartGestureListener = this //Check whether finger down/up
        chart.isHighlightPerDragEnabled = true //Crosshair only when dragging
        chart.isHighlightPerTapEnabled = false //Don't persist crosshair
        chart.isDragEnabled = true
        chart.isDoubleTapToZoomEnabled = false
        chart.setPinchZoom(false)
        chart.setScaleEnabled(false)
        chart.setDrawBorders(false)
        chart.setOnChartValueSelectedListener(this) //change price textview
        bottomLine.setBackgroundColor(chartLineColor)
    }

    private fun styleXAxis() {
        with(chart.xAxis) {
            setDrawLabels(true)
            setDrawGridLines(false)
            setDrawAxisLine(true)
            valueFormatter = DateFormatter(Period.DAY)
            position = XAxis.XAxisPosition.BOTTOM
            yOffset = 2f
            axisLineColor = chartLineColor
            textColor = this@CryptoChart.textColor
            axisLineWidth = 1f
            textSize = 12f
            setLabelCount(8, true)
        }
    }

    private fun styleYAxis() {
        with(chart.axisLeft) {
            setDrawAxisLine(false)
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            xOffset = 15.0f
            valueFormatter = IAxisValueFormatter { _, _ -> "" }
            axisLineColor = chartLineColor
            axisLineWidth = 1.0f
            gridColor = chartColor
            gridLineWidth = 1.0f
            spaceTop = 20.0f   //Translates the top lower so you can see high values better
            spaceBottom = 20.0f //Translates the bottoms higher so you can see the floor better
            textColor = this@CryptoChart.textColor
            textSize = 12.0f
            setLabelCount(5, true)
            setCenterAxisLabels(false)
        }
    }

    private fun getWidth(text: String, fontSize: Float = 14f, typeface: Typeface = Typeface.DEFAULT): Float {
        val textPaint = Paint()
        textPaint.typeface = typeface
        textPaint.textSize = fontSize
        val width = textPaint.measureText(text)
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, metrics)
    }

    override fun onChartDoubleTapped(me: MotionEvent?) {}
    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}
    override fun onChartLongPressed(me: MotionEvent?) {}
    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
    override fun onChartSingleTapped(me: MotionEvent?) {}
    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
    override fun onNothingSelected() {}
}