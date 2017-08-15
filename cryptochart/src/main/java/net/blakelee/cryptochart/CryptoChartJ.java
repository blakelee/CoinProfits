package net.blakelee.cryptochart;
/*
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CryptoChartJ extends RelativeLayout implements OnChartGestureListener, OnChartValueSelectedListener {

    public CryptoChartJ(Context context) {
        super(context);
        init();
    }

    public CryptoChartJ(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public CryptoChartJ(Context context, AttributeSet attr, Integer defStyleAttr) {
        super(context, attr, defStyleAttr);
        init();
    }

    @LayoutRes
    Integer pricesLayout = R.layout.crypto_chart;

    View view = LayoutInflater.from(getContext()).inflate(pricesLayout, this, true);
    RelativeLayout prices_container = findViewById(R.id.prices_container);
    TextView price_high = findViewById(R.id.price_high);
    TextView price_mid = findViewById(R.id.price_high);
    TextView price_low = findViewById(R.id.price_high);
    LineChart chart = findViewById(R.id.stacked_area_chart);

    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
    Float lineLength = 5000f;
    Float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, metrics);

    Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
    Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);

    Integer textColor = Color.GRAY;
    Integer lineColor = Color.WHITE;
    Integer chartColor = Color.BLACK;
    IAxisValueFormatter valueFormatter = null;
    Drawable fillDrawable = null;

    interface TouchListener {
        void onValueSelected(Entry entry);

        void onChartGestureStart();

        void onChartGestureEnd();
    }

    void init() {
        styleChart();
        styleYAxis();
        styleXAxis();
        setPrices();
    }

    void setFormatter(IAxisValueFormatter formatter) {
        chart.getXAxis().setValueFormatter(formatter);
    }

    void setLabelCount(Integer num, Boolean force) {
        chart.getXAxis().setLabelCount(num, force);
    }

    public void setData(List<List<Float>> data) {
        List<Entry> dataSet = new ArrayList<>();
        Iterator<List<Float>> iterator = data.iterator();

        while (iterator.hasNext()) {
            List<Float> cur = iterator.next();
            dataSet.add(new Entry(cur.get(0), cur.get(1)));
        }

        LineDataSet lineDataSet = new LineDataSet(dataSet, "");
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(lineColor);
        lineDataSet.setFillDrawable(fillDrawable);
        lineDataSet.setDrawFilled(true);

        List<ILineDataSet> iLineData = new ArrayList<>();
        iLineData.add(lineDataSet);

        LineData lineData = new LineData(iLineData);

        chart.setData(lineData);
        Float min = chart.getYChartMin();
        Float max = chart.getYChartMax();

        Float step = (max - min) / 4;

        Float y1 = min + step;
        Float y2 = min + 2 * step;
        Float y3 = min + 3 * step;

        List<Float> yValues = new ArrayList<>();
        yValues.add(y1);
        yValues.add(y2);
        yValues.add(y3);

        setPrices(yValues);
    }

    private void setPrices(List<Float> prices) {
        Float width = 0f;
        Iterator<Float> iterator = prices.iterator();

        while (iterator.hasNext()) {
            Float cur = iterator.next();
            width = Math.max(width, getWidth("$" + cur, 14f, Typeface.DEFAULT));
        }

        price_low.setText(String.format("$%s", decimalFormat(prices.get(0))));
        price_mid.setText(String.format("$%s", decimalFormat(prices.get(1))));
        price_high.setText(String.format("$%s", decimalFormat(prices.get(2))));
        chart.getAxisLeft().enableGridDashedLine(lineLength, width, lineLength);
    }

    private void setPrices() {
        price_high.setText("");
        price_mid.setText("");
        price_low.setText("");
        chart.getAxisLeft().enableGridDashedLine(lineLength, 0f, lineLength);
    }

    private void handleGestureEnd(MotionEvent me) {
        if (me != null) {
            if (me.getAction() == MotionEvent.ACTION_DOWN ||
                    me.getAction() == MotionEvent.ACTION_UP ||
                    me.getAction() == MotionEvent.ACTION_CANCEL) {
                Boolean isDown = me.getAction() != MotionEvent.ACTION_DOWN;
                chart.getAxisLeft().setEnabled(isDown);
            }
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        prices_container.startAnimation(slideOut);
        prices_container.setVisibility(View.GONE);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        chart.highlightValues(null);
        handleGestureEnd(me);
        prices_container.startAnimation(slideIn);
        prices_container.setVisibility(View.VISIBLE);
    }

    private void styleChart() {
        chart.setExtraBottomOffset(2f);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setMinOffset(0f);
        chart.getAxisRight().setEnabled(false);
        chart.setOnChartGestureListener(this);
        chart.setHighlightPerDragEnabled(true);
        chart.setHighlightPerTapEnabled(false);
        chart.setDragEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBorders(false);
        chart.setGridBackgroundColor(Color.BLUE);
        chart.setOnChartValueSelectedListener(this);
    }

    private void styleXAxis() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new DateFormatter(Period.DAY));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(2f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextColor(textColor);
        xAxis.setAxisLineWidth(1f);
        xAxis.setTextSize(12f);
        xAxis.setLabelCount(8, true);
    }

    private void styleYAxis() {
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setXOffset(15.0f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });
        yAxis.setAxisLineColor(lineColor);
        yAxis.setAxisLineWidth(1.0f);
        yAxis.setGridColor(chartColor);
        yAxis.setGridLineWidth(1.0f);
        yAxis.setSpaceTop(20.0f); //Translates the top lower so you can see high values better
        yAxis.setSpaceBottom(20.0f); //Translates the bottoms higher so you can see the floor better
        yAxis.setTextColor(textColor);
        yAxis.setTextSize(12.0f);
        yAxis.setLabelCount(5, true);
        yAxis.setCenterAxisLabels(false);
    }

    private Float getWidth(String text, Float fontSize, Typeface typeface) {
        Paint textPaint = new Paint();
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(fontSize);
        Float width = textPaint.measureText(text);
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, metrics);
    }

    private String decimalFormat(Float number) {
        NumberFormat format = NumberFormat.getInstance();
        String[] splitter = String.format(Locale.getDefault(), "%f", number).split("\\.");
        format.setMinimumFractionDigits(2);

        if (number < 1) {
            Integer places = 0;
            String str = splitter[0];

            if (str.length() > 2)
                for (Integer i = 0; i < str.length(); ++i) {
                    if (str.charAt(i) != '0' && places >= 2)
                        break;
                    places++;
                }
            else
                places = 2;

            format.setMaximumFractionDigits(places);
        }

        return format.format(number);
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {}

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}

    @Override
    public void onChartSingleTapped(MotionEvent me) {}

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {}

    @Override
    public void onNothingSelected() {}
}*/