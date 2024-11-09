package com.example.watermanagementsystem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportsGraphView extends View {
    private Paint linePaint;
    private Paint dotPaint;
    private Paint textPaint;
    private Paint gridPaint;

    private List<String> labels = new ArrayList<> ();
    private List<Integer> values = new ArrayList<> ();
    private int maxValue = 0;


    private float leftPadding;
    private float rightPadding;
    private float topPadding;
    private float bottomPadding;
    private float labelRotationAngle = - 45;

    private Rect textBounds = new Rect ();

    public ReportsGraphView (Context context) {
        super (context);
        init ();
    }

    public ReportsGraphView (Context context, AttributeSet attrs) {
        super (context, attrs);
        init ();
    }

    private void init () {

        linePaint = new Paint ();
        linePaint.setColor (ContextCompat.getColor (getContext (), R.color.colorPrimary));
        linePaint.setStyle (Paint.Style.STROKE);
        linePaint.setStrokeWidth (8f);
        linePaint.setAntiAlias (true);

        dotPaint = new Paint ();
        dotPaint.setColor (ContextCompat.getColor (getContext (), R.color.colorPrimary));
        dotPaint.setStyle (Paint.Style.FILL);
        dotPaint.setAntiAlias (true);

        textPaint = new Paint ();
        textPaint.setColor (ContextCompat.getColor (getContext (), android.R.color.black));
        textPaint.setTextSize (dpToPx (14));
        textPaint.setAntiAlias (true);

        gridPaint = new Paint ();
        gridPaint.setColor (ContextCompat.getColor (getContext (), android.R.color.darker_gray));
        gridPaint.setAlpha (100);
        gridPaint.setStyle (Paint.Style.STROKE);
        gridPaint.setStrokeWidth (2f);


        leftPadding = dpToPx (70);
        rightPadding = dpToPx (30);
        topPadding = dpToPx (30);
        bottomPadding = dpToPx (90);
    }

    public void setData (Map<String, Integer> data) {
        if ( data == null || data.isEmpty () ) {
            Log.d ("ReportsGraphView", "Received empty or null data");
            return;
        }

        labels = new ArrayList<> (data.keySet ());
        values = new ArrayList<> (data.values ());

        // Find maximum value
        maxValue = 0;
        for ( int value : values ) {
            maxValue = Math.max (maxValue, value);
        }

        // Ensure minimum scale
        maxValue = Math.max (maxValue, 1);
        // Round up to next multiple of 5 for better scaling
        maxValue = ( ( maxValue + 4 ) / 5 ) * 5;

        Log.d ("ReportsGraphView", "Data set with " + values.size () + " points, max value: " + maxValue);
        invalidate ();
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw (canvas);

        if ( labels.isEmpty () || values.isEmpty () ) return;

        float width = getWidth ();
        float height = getHeight ();
        float graphHeight = height - ( topPadding + bottomPadding );
        float graphWidth = width - ( leftPadding + rightPadding );


        drawGrid (canvas, graphHeight, graphWidth);


        drawDataPoints (canvas, graphHeight, graphWidth);


        drawXAxisLabels (canvas, graphHeight, graphWidth);
    }

    private void drawGrid (Canvas canvas, float graphHeight, float graphWidth) {
        int gridLines = 5;
        float gridSpacing = graphHeight / gridLines;

        for ( int i = 0; i <= gridLines; i++ ) {
            float y = topPadding + ( i * gridSpacing );

            canvas.drawLine (leftPadding, y, getWidth () - rightPadding, y, gridPaint);

            String gridLabel = String.valueOf (maxValue - ( ( maxValue / gridLines ) * i ));
            textPaint.getTextBounds (gridLabel, 0, gridLabel.length (), textBounds);
            float labelX = leftPadding - textBounds.width () - dpToPx (5);
            float labelY = y + ( textBounds.height () / 2f );
            canvas.drawText (gridLabel, labelX, labelY, textPaint);
        }
    }

    private void drawDataPoints (Canvas canvas, float graphHeight, float graphWidth) {
        Path path = new Path ();
        float xInterval = graphWidth / ( Math.max (1, labels.size () - 1) );

        for ( int i = 0; i < values.size (); i++ ) {
            float x = leftPadding + ( i * xInterval );
            float y = topPadding + graphHeight - ( ( values.get (i) * graphHeight ) / maxValue );

            if ( i == 0 ) {
                path.moveTo (x, y);
            } else {
                path.lineTo (x, y);
            }

            canvas.drawCircle (x, y, dpToPx (4), dotPaint);
        }

        canvas.drawPath (path, linePaint);
    }

    private void drawXAxisLabels (Canvas canvas, float graphHeight, float graphWidth) {
        float xInterval = graphWidth / ( Math.max (1, labels.size () - 1) );

        canvas.save ();
        for ( int i = 0; i < labels.size (); i++ ) {
            String label = labels.get (i);
            float x = leftPadding + ( i * xInterval );
            float y = getHeight () - dpToPx (10);


            canvas.save ();
            canvas.rotate (labelRotationAngle, x, y);

            textPaint.getTextBounds (label, 0, label.length (), textBounds);
            canvas.drawText (label, x - ( textBounds.width () / 2f ), y, textPaint);

            canvas.restore ();
        }
        canvas.restore ();
    }

    private float dpToPx (float dp) {
        return dp * getResources ().getDisplayMetrics ().density;
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) dpToPx (400);
        int desiredHeight = (int) dpToPx (300);

        int width = resolveSize (desiredWidth, widthMeasureSpec);
        int height = resolveSize (desiredHeight, heightMeasureSpec);

        setMeasuredDimension (width, height);
    }

}