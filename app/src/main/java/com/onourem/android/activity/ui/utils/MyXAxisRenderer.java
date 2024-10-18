package com.onourem.android.activity.ui.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.onourem.android.activity.R;

public class MyXAxisRenderer extends XAxisRenderer {
    private final Context context;
    Paint drawPaint = new Paint();

    public MyXAxisRenderer(Context context, ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
        this.context = context;

        drawPaint.setColor(Color.LTGRAY);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(1);
        drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void drawLabel(Canvas canvas, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {

        Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setShadowLayer(5.5f, 6.0f, 6.0f, Color.LTGRAY);

        TextPaint mLetterPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mLetterPaint.setColor(Color.BLACK);
        mLetterPaint.setTextSize(30);
        mLetterPaint.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat_bold));

        mLetterPaint.setTextAlign(Paint.Align.CENTER);

        Rect textMathRect = new Rect();
        mLetterPaint.getTextBounds(formattedLabel, 0, 1, textMathRect);
        float mLetterTop = textMathRect.height() / 2f;

        canvas.drawCircle(
                x, y,
                40, mCirclePaint);
        canvas.drawText(formattedLabel,
                x, mLetterTop + y,
                mLetterPaint);
        canvas.save();
//        super.drawLabel(canvas, formattedLabel, x, y, anchor, angleDegrees);
    }
}
