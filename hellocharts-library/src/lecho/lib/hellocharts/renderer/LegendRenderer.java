package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Legend;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.AxisAutoValues;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.util.FloatUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Default axes renderer. Can draw maximum four axes - two horizontal(top/bottom) and two vertical(left/right).
 */
public class LegendRenderer {
    public int DEFAULT_LABEL_MARGIN_DP = 4;

    /**
     * Axis positions indexes, used for indexing tabs that holds axes parameters, see below.
     */
//    private static final int TOP = 0;
//    private static final int LEFT = 1;
//    private static final int RIGHT = 2;
//    private static final int BOTTOM = 3;

    /**
     * Used to measure label width. If label has mas 5 characters only 5 first characters of this array are used to
     * measure text width.
     */
//    private static final char[] labelWidthChars = new char[]{
//            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
//            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
//            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
//            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'};

    private Chart chart;
    private ChartComputator computator;
    private int axisMargin;
    protected int labelMargin;
    private float density;
//    private float scaledDensity;
//    private Paint[] labelPaintTab = new Paint[]{new Paint(), new Paint(), new Paint(), new Paint()};
//    private Paint[] namePaintTab = new Paint[]{new Paint(), new Paint(), new Paint(), new Paint()};
//    private Paint[] linePaintTab = new Paint[]{new Paint(), new Paint(), new Paint(), new Paint()};
//    private float[] nameBaselineTab = new float[4];
//    private float[] labelBaselineTab = new float[4];
//    private float[] separationLineTab = new float[4];
//    private int[] labelWidthTab = new int[4];
//    private int[] labelTextAscentTab = new int[4];
//    private int[] labelTextDescentTab = new int[4];
//    private int[] labelDimensionForMarginsTab = new int[4];
//    private int[] labelDimensionForStepsTab = new int[4];
//    private int[] tiltedLabelXTranslation = new int[4];
//    private int[] tiltedLabelYTranslation = new int[4];
//    private FontMetricsInt[] fontMetricsTab = new FontMetricsInt[]{new FontMetricsInt(), new FontMetricsInt(),
//            new FontMetricsInt(), new FontMetricsInt()};
    
    /**
     * Font metrics for label paint, used to determine text height.
     */
    protected FontMetricsInt fontMetrics = new FontMetricsInt();
    
    private char[] labelBuffer;
    
    /**
     * Paint for value labels.
     */
    protected Paint labelPaint = new Paint();
    
    /**
     * Holds coordinates for label background rect.
     */
    protected RectF labelBackgroundRect = new RectF();
    /**
     * Paint for labels background.
     */
    protected Paint labelBackgroundPaint = new Paint();
    
    protected boolean isValueLabelBackgroundEnabled;
    protected boolean isValueLabelBackgroundAuto;

    public LegendRenderer(Context context, Chart chart) {
        this.chart = chart;
        computator = chart.getChartComputator();
        density = context.getResources().getDisplayMetrics().density;
//        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        axisMargin = ChartUtils.dp2px(density, DEFAULT_LABEL_MARGIN_DP);  
        labelMargin = ChartUtils.dp2px(density, DEFAULT_LABEL_MARGIN_DP);
        
        labelPaint.setAntiAlias(true);
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setTextAlign(Align.LEFT);
        labelPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        labelPaint.setColor(Color.WHITE);
        
        isValueLabelBackgroundEnabled = true;
        isValueLabelBackgroundAuto = true;
    }
    
    private void initMargin() {
        int margin = 100;
        margin += axisMargin + Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);
        insetContentRectWithMargins(margin);
    }
    
    private void insetContentRectWithMargins(int axisMargin) {
    	chart.getChartComputator().insetContentLegendsRect(0, 0, 0, axisMargin);
    }
    
    /**
     * Draw axes labels and names in the foreground.
     *
     * @param canvas
     */
    public void drawInForeground(Canvas canvas) {
    	 for(Legend legend : chart.getChartData().getLegends()){
    		 drawLegend(canvas, legend);
    	 }
//        Axis axis = chart.getChartData().getAxisYLeft();
//        if (null != axis) {
//            drawAxisLabelsAndName(canvas, axis, LEFT);
//        }
//
//        axis = chart.getChartData().getAxisYRight();
//        if (null != axis) {
//            drawAxisLabelsAndName(canvas, axis, RIGHT);
//        }
//
//        axis = chart.getChartData().getAxisXBottom();
//        if (null != axis) {
//            drawAxisLabelsAndName(canvas, axis, BOTTOM);
//        }
//
//        axis = chart.getChartData().getAxisXTop();
//        if (null != axis) {
//            drawAxisLabelsAndName(canvas, axis, TOP);
//        }
    }
    
    private void drawLegend(Canvas canvas, Legend legend) {
        final Rect contentRect = computator.getContentRectMinusLegendsMargins();
        float rawX = contentRect.centerX(), rawY = contentRect.bottom, offset = 0;
        labelBuffer = legend.getLegendName().toCharArray();
        final int numChars = labelBuffer.length;
        if (numChars == 0) {
            // No need to draw empty label
            return;
        }

        final float labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.length - numChars, numChars);
        final int labelHeight = Math.abs(fontMetrics.ascent);
        float left = rawX - labelWidth / 2 - labelMargin;
        float right = rawX + labelWidth / 2 + labelMargin;

        float top;
        float bottom;

//        if (10 >= 100) {
            top = rawY - offset - labelHeight - labelMargin * 2;
            bottom = rawY - offset;
//        } else {
//            top = rawY + offset;
//            bottom = rawY + offset + labelHeight + labelMargin * 2;
//        }

        if (top < contentRect.top) {
            top = rawY + offset;
            bottom = rawY + offset + labelHeight + labelMargin * 2;
        }
        if (bottom > contentRect.bottom) {
            top = rawY - offset - labelHeight - labelMargin * 2;
            bottom = rawY - offset;
        }
        if (left < contentRect.left) {
            left = rawX;
            right = rawX + labelWidth + labelMargin * 2;
        }
        if (right > contentRect.right) {
            left = rawX - labelWidth - labelMargin * 2;
            right = rawX;
        }

        labelBackgroundRect.set(left, top, right, bottom);
        drawLabelTextAndBackground(canvas, labelBuffer, labelBuffer.length - numChars, numChars,
                legend.getLegendColor());
    }
    
    public void onChartSizeChanged() {
        onChartDataOrSizeChanged();
    }

    public void onChartDataChanged() {
        onChartDataOrSizeChanged();
    }

    private void onChartDataOrSizeChanged() {
    	labelPaint.getFontMetricsInt(fontMetrics);
    	initMargin();
    }
    
    /**
     * Draws label text and label background if isValueLabelBackgroundEnabled is true.
     */
    protected void drawLabelTextAndBackground(Canvas canvas, char[] labelBuffer, int startIndex, int numChars,
                                              int autoBackgroundColor) {
        final float textX;
        final float textY;

        if (isValueLabelBackgroundEnabled) {

            if (isValueLabelBackgroundAuto) {
                labelBackgroundPaint.setColor(autoBackgroundColor);
            }

            canvas.drawRect(labelBackgroundRect, labelBackgroundPaint);

            textX = labelBackgroundRect.left + labelMargin;
            textY = labelBackgroundRect.bottom - labelMargin;
        } else {
            textX = labelBackgroundRect.left;
            textY = labelBackgroundRect.bottom;
        }

        canvas.drawText(labelBuffer, startIndex, numChars, textX, textY, labelPaint);
    }

}