package com.onourem.android.activity.ui.utils.range_seekbar;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.onourem.android.activity.R;

/**
 * RangeSeekBar is an extension {@link FrameLayout} that adds two draggable thumbs.
 * The user can touch the thumbs and drag left or right to set the start and the end progress level.
 * Clients of the {@link RangeSeekBar} can attach a {@link OnRangeSeekBarListener} to be notified of the user's actions.
 */
public class RangeSeekBar extends FrameLayout {

    private Thumb thumbStart;
    private float progress1 = 0;

    private Thumb thumbEnd;
    private float progress2 = 50;

    private FrameLayout container;

    private int minDifference = 20;

    private final Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int rangeColor;

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int trackColor;

    private OnRangeSeekBarListener callback;

    private LayoutParams containerLayoutParams;

    private int maxProgress = 100;
    private final OnTouchListener thumb1Touch = new OnTouchListener() {
        private float t1X;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case ACTION_UP:
                    thumbStart.setPressed(false);
                    view.performClick();
                    break;

                case ACTION_DOWN:
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    t1X = motionEvent.getX() - motionEvent.getRawX() + thumbStart.getTranslationX();
                    thumbStart.setPressed(true);
                    break;

                case ACTION_MOVE:
                    float dx = t1X + motionEvent.getRawX();
                    dx = Math.max(thumbStart.getHalfThumbWidth(), dx);
                    progress1 = (((dx - (thumbStart.getHalfThumbWidth())) / (container.getWidth() - thumbStart.getThumbWidth())) * maxProgress);
                    if (progress1 >= progress2 - minDifference) {
                        progress1 = progress2 - minDifference;
                    }

                    if (callback != null) {
                        callback.onRangeValues(RangeSeekBar.this, (int) progress1, (int) progress2);
                    }
                    invalidate();

                    break;

                default:
                    return false;

            }
            return true;
        }
    };
    private final OnTouchListener thumb2Touch = new OnTouchListener() {
        private float t2X;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case ACTION_UP:
                    thumbEnd.setPressed(false);
                    view.performClick();
                    break;

                case ACTION_DOWN:
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    t2X = motionEvent.getX() - motionEvent.getRawX() + thumbEnd.getTranslationX();
                    thumbEnd.setPressed(true);
                    break;

                case ACTION_MOVE:
                    float dx = t2X + motionEvent.getRawX();
                    dx = Math.min(dx, container.getWidth() - thumbEnd.getHalfThumbWidth());
                    progress2 = (((dx - (thumbEnd.getHalfThumbWidth())) / (container.getWidth() - thumbEnd.getThumbWidth())) * maxProgress);
                    if (progress2 <= progress1 + minDifference) {
                        progress2 = progress1 + minDifference;
                    }

                    if (callback != null) {
                        callback.onRangeValues(RangeSeekBar.this, (int) progress1, (int) progress2);
                    }
                    invalidate();

                    break;

                default:
                    return false;

            }
            return true;
        }
    };

    public RangeSeekBar(Context context) {
        this(context, null);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        int containerHeight = getResources().getDimensionPixelSize(R.dimen.container_height);
        int containerMargin = getResources().getDimensionPixelSize(R.dimen.container_margin);

        container = new FrameLayout(context);
        containerLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, containerHeight);
        containerLayoutParams.gravity = Gravity.CENTER;
        containerLayoutParams.leftMargin = containerMargin;
        containerLayoutParams.rightMargin = containerMargin;
        addView(container, containerLayoutParams);


        thumbStart = new Thumb(context);
        LayoutParams thumbStartLayoutParams = new LayoutParams(thumbStart.getThumbWidth(), thumbStart.getThumbWidth());
        thumbStartLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(thumbStart, thumbStartLayoutParams);
        thumbStart.setOnTouchListener(thumb1Touch);

        thumbEnd = new Thumb(context);
        LayoutParams thumbEndLayoutParams = new LayoutParams(thumbEnd.getThumbWidth(), thumbEnd.getThumbWidth());
        thumbEndLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(thumbEnd, thumbEndLayoutParams);
        thumbEnd.setOnTouchListener(thumb2Touch);

        rangeColor = ContextCompat.getColor(context, R.color.colorAccent);
        rangePaint.setColor(rangeColor);
        rangePaint.setStyle(Paint.Style.STROKE);
        rangePaint.setStrokeWidth(context.getResources().getDimension(R.dimen.line));

        trackColor = ContextCompat.getColor(context, android.R.color.darker_gray);
        trackPaint.setColor(trackColor);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.line));
        trackPaint.setAlpha(130);

        thumbStart.setDisableCircleColor(trackColor);
        thumbEnd.setDisableCircleColor(trackColor);

    }

    /**
     * Set the color of the two thumbs and the range line connecting the two thumbs.
     * Default color is {@code R.attr.colorControlActivated}
     *
     * @param color The color that replace default range color
     */
    public void setRangeColor(int color) {
        rangeColor = color;
        rangePaint.setColor(color);
        thumbStart.setColor(color);
        thumbEnd.setColor(color);
        invalidate();
    }

    /**
     * Set the color of the track.
     * Default color is {@code android.R.color.darker_gray} with alpha {@code 130/255 }
     * The color you replace will be fully opaque unless you set a color with an alpha component
     * <br>
     * <br> i.e. {@code Color.parseColor("#77000000")}
     *
     * @param color The color that replace default track color
     */
    public void setTrackColor(int color) {
        trackColor = color;
        trackPaint.setColor(color);
        thumbStart.setDisableCircleColor(color);
        thumbEnd.setDisableCircleColor(color);
        invalidate();
    }

    public void setMax(int max) {
        maxProgress = max;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float dx1 = getDeltaX(thumbStart, progress1);
        thumbStart.setTranslationX(dx1);

        float dx2 = getDeltaX(thumbEnd, progress2);
        thumbEnd.setTranslationX(dx2);

        if (dx1 > (rangePaint.getStrokeWidth() * 3)) {
            canvas.drawLine(thumbStart.getHalfThumbWidth() + containerLayoutParams.leftMargin,
                    getHeight() / 2,
                    dx1 + thumbStart.getHalfThumbWidth() + containerLayoutParams.leftMargin - (rangePaint.getStrokeWidth() * 3),
                    getHeight() / 2,
                    trackPaint);
        }

        canvas.drawLine(dx1 + thumbStart.getHalfThumbWidth() + containerLayoutParams.leftMargin + (rangePaint.getStrokeWidth() * 3),
                getHeight() / 2,
                dx2 + thumbEnd.getHalfThumbWidth() + containerLayoutParams.rightMargin - (rangePaint.getStrokeWidth() * 3),
                getHeight() / 2,
                trackPaint);

        if (container.getWidth() - containerLayoutParams.leftMargin - containerLayoutParams.rightMargin > dx2 + thumbEnd.getHalfThumbWidth() + containerLayoutParams.rightMargin + (rangePaint.getStrokeWidth() * 3)) {
            canvas.drawLine(dx2 + thumbEnd.getHalfThumbWidth() + containerLayoutParams.rightMargin + (rangePaint.getStrokeWidth() * 3),
                    getHeight() / 2,
                    container.getWidth() - containerLayoutParams.leftMargin - containerLayoutParams.rightMargin,
                    getHeight() / 2,
                    trackPaint);
        }

        canvas.drawLine(dx1 + thumbStart.getHalfThumbWidth() + containerLayoutParams.leftMargin,
                getHeight() / 2,
                dx2 + thumbEnd.getHalfThumbWidth() + containerLayoutParams.rightMargin,
                getHeight() / 2,
                rangePaint);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        trackPaint.setAlpha(enabled ? 130 : 80);
        rangePaint.setAlpha(enabled ? 255 : 0);
        thumbStart.setOnTouchListener(enabled ? thumb1Touch : null);
        thumbStart.setEnabled(enabled);
        thumbEnd.setOnTouchListener(enabled ? thumb2Touch : null);
        thumbEnd.setEnabled(enabled);
    }

    private float getDeltaX(Thumb thumb, float progress) {
        return (container.getWidth() - thumb.getThumbWidth()) / (float) maxProgress * progress;
    }

    /**
     * Set the min progress threshold between the two Thumbs
     *
     * @param difference the min progress threshold between the two Thumbs
     */
    public void setMinDifference(int difference) {
        minDifference = difference;
    }

    public void setOnRangeSeekBarListener(OnRangeSeekBarListener listener) {
        callback = listener;
    }

    public int getStartProgress() {
        return (int) progress1;
    }

    /**
     * Set the progress and the position of left Thumb
     *
     * @param progress the progress value of left Thumb
     */
    public void setStartProgress(int progress) {
        progress1 = progress;
        invalidate();
    }

    public int getEndProgress() {
        return (int) progress2;
    }

    /**
     * Set the progress and the position of right Thumb
     *
     * @param progress the progress value of right Thumb
     */
    public void setEndProgress(int progress) {
        progress2 = progress;
        invalidate();
    }


}
