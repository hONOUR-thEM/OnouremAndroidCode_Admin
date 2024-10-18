package com.onourem.android.activity.ui.utils.bubbleindicator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.onourem.android.activity.R;

public class BubblePageIndicator extends MotionIndicator implements ViewPager.OnPageChangeListener,
        ViewPager.OnAdapterChangeListener {
    private static final long ANIMATION_TIME = 300;
    private static final int SWIPE_RIGHT = 1000;
    private static final int SWIPE_LEFT = 1001;

    private static final int ANIMATE_SHIFT_LEFT = 2000;
    private static final int ANIMATE_SHIFT_RIGHT = 2001;
    private static final int ANIMATE_IDLE = 2002;

    private static final float ADD_RADIUS_DEFAULT = 1;
    private final Paint paintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint paintFill = new Paint(ANTI_ALIAS_FLAG);
    private int onSurfaceCount;
    private int risingCount;
    private int surfaceStart;
    private int surfaceEnd;
    private float radius;
    private float marginBetweenCircles;
    private int scrollState;
    private float scaleRadiusCorrection = ADD_RADIUS_DEFAULT;

    private ValueAnimator translationAnim;

    private int startX = Integer.MIN_VALUE;
    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            ensureState();
            forceLayoutChanges();
        }
    };
    private float offset;
    private int swipeDirection;
    private int animationState = ANIMATE_IDLE;

    public BubblePageIndicator(Context context) {
        this(context, null);
    }

    public BubblePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.bpi_indicatorStyle);
    }

    public BubblePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubblePageIndicator, defStyle, 0);

        if (a != null) {
            paintPageFill.setStyle(Style.FILL);
            paintPageFill.setColor(a.getColor(R.styleable.BubblePageIndicator_bpi_pageColor, 0));
            paintFill.setStyle(Style.FILL);
            paintFill.setColor(a.getColor(R.styleable.BubblePageIndicator_bpi_fillColor, 0));
            radius = a.getDimensionPixelSize(R.styleable.BubblePageIndicator_bpi_radius, 0);
            marginBetweenCircles = a.getDimensionPixelSize(R.styleable.BubblePageIndicator_bpi_marginBetweenCircles, 0);
            onSurfaceCount = a.getInteger(R.styleable.BubblePageIndicator_bpi_onSurfaceCount, 0);
            risingCount = a.getInteger(R.styleable.BubblePageIndicator_bpi_risingCount, 0);

            a.recycle();

            surfaceStart = 0;
            surfaceEnd = onSurfaceCount - 1;
        }

    }

    private void ensureState() {
        correctSurfaceIfDataSetChanges();
        if (currentPage >= getCount() && getCount() != 0) {
            currentPage = getCount() - 1;
        }
        correctStartXOnDataSetChanges();
    }

    private void correctSurfaceIfDataSetChanges() {
        if (onSurfaceCount != surfaceEnd - surfaceStart + 1) {
            surfaceStart = currentPage;
            surfaceEnd = surfaceStart + onSurfaceCount - 1;
        }
        if (getCount() == 0) return;
        if (surfaceEnd > getCount() - 1) {
            if (getCount() > onSurfaceCount) {
                surfaceEnd = getCount() - 1;
                surfaceStart = surfaceEnd - (onSurfaceCount - 1);
            } else {
                surfaceEnd = onSurfaceCount - 1;
                surfaceStart = 0;
            }
        }
    }

    private void correctStartXOnDataSetChanges() {
        if (startX == Integer.MIN_VALUE) {
            return;
        }
        int initial = getInitialStartX();
        if (startX == initial) {
            return;
        }
        if (surfaceEnd > onSurfaceCount - 1) {
            initial -= (surfaceEnd - (onSurfaceCount - 1)) * (marginBetweenCircles + radius * 2);
            if (getCount() - onSurfaceCount <= 1) {
                initial -= marginBetweenCircles + radius * 2;
            }
        }
        startX = initial;
    }

    public void setScaleRadiusCorrection(float value) {
        scaleRadiusCorrection = value;
        forceLayoutChanges();
    }

    public void setOnSurfaceCount(int onSurfaceCount) {
        this.onSurfaceCount = onSurfaceCount;
        ensureState();
        forceLayoutChanges();
    }

    public void setRisingCount(int risingCount) {
        this.risingCount = risingCount;
        forceLayoutChanges();
    }

    @ColorInt
    public int getPageColor() {
        return paintPageFill.getColor();
    }

    public void setPageColor(@ColorInt int pageColor) {
        paintPageFill.setColor(pageColor);
        invalidate();
    }

    @ColorInt
    public int getFillColor() {
        return paintFill.getColor();
    }

    public void setFillColor(@ColorInt int fillColor) {
        paintFill.setColor(fillColor);
        invalidate();
    }

    public void setMarginBetweenCircles(@Dimension float margin) {
        this.marginBetweenCircles = margin;
        forceLayoutChanges();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(@Dimension float radius) {
        this.radius = radius;
        forceLayoutChanges();
    }

    @Override
    protected int getCount() {
        if (viewPager == null || viewPager.getAdapter() == null) {
            return 0;
        }
        return viewPager.getAdapter().getCount();
    }

    @Override
    public int getPaddingLeft() {
        return (int) Math.max(super.getPaddingLeft(), marginBetweenCircles);
    }

    @Override
    public int getPaddingRight() {
        return (int) Math.max(super.getPaddingRight(), marginBetweenCircles);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewPager == null) {
            return;
        }

        final int count = getCount();
        if (count == 0 || count == 1) {
            return;
        }

        int shortPaddingBefore = getPaddingTop();
        final float shortOffset = shortPaddingBefore + radius + 1;

        //Draw stroked circles
        drawStrokedCircles(canvas, count, shortOffset, startX);

        //Draw the filled circle according to the current scroll
        drawFilledCircle(canvas, shortOffset, startX);
    }

    private void drawStrokedCircles(Canvas canvas, int count, float shortOffset, float startX) {
        // Only paint fill if not completely transparent
        if (paintPageFill.getAlpha() == 0) {
            return;
        }
        float dX;
        for (int iLoop = 0; iLoop < count; iLoop++) {
            if (iLoop < surfaceStart - risingCount) continue;
            if (iLoop > surfaceEnd + risingCount) return;
            dX = startX + iLoop * (radius * 2 + marginBetweenCircles);

            if (dX < 0 || dX > getWidth()) continue;

            float scaledRadius = getScaledRadius(radius, iLoop);

            canvas.drawCircle(dX, shortOffset, scaledRadius, paintPageFill);
        }
    }

    private float getScaledRadius(float radius, int position) {
        // circles to the left of the surface
        if (position < surfaceStart) {
            float add = ((surfaceStart - position == 1) ? scaleRadiusCorrection : 0);
            // swipe left
            if (swipeDirection == SWIPE_LEFT && animationState == ANIMATE_SHIFT_LEFT) {
                float finalRadius = radius / (2 << (surfaceStart - position - 1)) + add;
                float currentRadius = radius / (2 << (surfaceStart - position - 1)) * 2 + ((surfaceStart - position - 1 == 1) ? 1 : 0);
                return currentRadius - (1 - offset) * (currentRadius - finalRadius);
            } else if (swipeDirection == SWIPE_RIGHT && animationState == ANIMATE_SHIFT_RIGHT) { // swipe right
                float finalRadius = radius / (2 << (surfaceStart - position - 1)) + add;
                float currentRadius = radius / (2 << (surfaceStart - position));
                return currentRadius + (1 - offset) * (finalRadius - currentRadius);
            } else {
                return radius / (2 << (surfaceStart - position - 1)) + add;
            }
        } else if (position > surfaceEnd) { // circles to the right of the surface
            float add = ((position - surfaceEnd == 1) ? scaleRadiusCorrection : 0);
            // swipe left
            if (swipeDirection == SWIPE_LEFT && animationState == ANIMATE_SHIFT_LEFT) {
                float finalRadius = radius / (2 << (position - surfaceEnd)) * 2 + add;
                float currentRadius = radius / (2 << (position - surfaceEnd));
                return currentRadius + (1 - offset) * (finalRadius - currentRadius);
            } else if (swipeDirection == SWIPE_RIGHT && animationState == ANIMATE_SHIFT_RIGHT) { // swipe right
                float finalRadius = radius / (2 << (position - surfaceEnd - 1)) + add;
                return finalRadius + offset * finalRadius;
            } else {
                return radius / (2 << (position - surfaceEnd - 1)) + add;
            }
        } else if (position == currentPage) {
            // swipe left
            if (swipeDirection == SWIPE_LEFT && animationState == ANIMATE_SHIFT_LEFT) {
                float finalRadius = radius + scaleRadiusCorrection;
                float currentRadius = radius / 2 + scaleRadiusCorrection;
                return currentRadius + (1 - offset) * (finalRadius - currentRadius);
            } else if (swipeDirection == SWIPE_RIGHT && animationState == ANIMATE_SHIFT_RIGHT) { // swipe right
                float finalRadius = radius + scaleRadiusCorrection;
                float currentRadius = radius / 2 + scaleRadiusCorrection;
                return currentRadius + (1 - offset) * (finalRadius - currentRadius);
            } else {
                return radius + scaleRadiusCorrection;
            }
        }
        return radius;
    }

    private void drawFilledCircle(Canvas canvas, float shortOffset, float startX) {
        float dX;
        float dY;
        float cx = currentPage * (radius * 2 + marginBetweenCircles);
        dX = startX + cx;
        dY = shortOffset;
        canvas.drawCircle(dX, dY, getScaledRadius(radius, currentPage), paintFill);
    }

    @SuppressWarnings("ConstantConditions")
    public void setViewPager(@NonNull ViewPager view) {
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(this);
            viewPager.removeOnAdapterChangeListener(this);
            try {
                viewPager.getAdapter().unregisterDataSetObserver(dataSetObserver);
            } catch (Exception ignore) {
            }
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        viewPager = view;
        viewPager.getAdapter().registerDataSetObserver(dataSetObserver);
        viewPager.addOnAdapterChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        forceLayoutChanges();
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        startX = Integer.MIN_VALUE;
        forceLayoutChanges();
    }

    private void forceLayoutChanges() {
        requestLayout();
        invalidate();
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item) {
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        if (item < 0 || item > getCount()) {
            return;
        }
        viewPager.setCurrentItem(item);
    }

    public void notifyDataSetChanged() {
        ensureState();
        forceLayoutChanges();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        scrollState = state;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (Math.abs(viewPager.getCurrentItem() - position) > 1) {
            // Inconsistency detected.
            // Probably we changed a page manually
            onPageManuallyChanged(viewPager.getCurrentItem());
            return;
        }
        if (position == currentPage) {
            if (positionOffset >= 0.5 && currentPage + 1 < getCount()) {
                swipeDirection = SWIPE_LEFT;
                currentPage += 1;
                if (currentPage > surfaceEnd) {
                    animationState = ANIMATE_SHIFT_LEFT;
                    correctSurface();
                    invalidate();
                    animateShifting(startX, (int) (startX - (marginBetweenCircles + radius * 2)));
                } else {
                    animationState = ANIMATE_IDLE;
                    invalidate();
                }
            }
        } else if (position < currentPage) {
            if (positionOffset <= 0.5) {
                swipeDirection = SWIPE_RIGHT;
                currentPage = position;
                if (currentPage < surfaceStart) {
                    animationState = ANIMATE_SHIFT_RIGHT;
                    correctSurface();
                    invalidate();
                    animateShifting(startX, (int) (startX + (marginBetweenCircles + radius * 2)));
                } else {
                    animationState = ANIMATE_IDLE;
                    invalidate();
                }
            }
        }
    }

    private void animateShifting(final int from, final int to) {
        if (translationAnim != null && translationAnim.isRunning()) translationAnim.end();
        translationAnim = ValueAnimator.ofInt(from, to);
        translationAnim.setDuration(ANIMATION_TIME);
        translationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translationAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                offset = (val - to) * 1f / (from - to);
                startX = val;
                invalidate();
            }
        });
        translationAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                animationState = ANIMATE_IDLE;
                startX = to;
                offset = 0;
                invalidate();
            }
        });
        translationAnim.start();
    }

    private void correctSurface() {
        if (currentPage > surfaceEnd) {
            surfaceEnd = currentPage;
            surfaceStart = surfaceEnd - (onSurfaceCount - 1);
        } else if (currentPage < surfaceStart) {
            surfaceStart = currentPage;
            surfaceEnd = surfaceStart + (onSurfaceCount - 1);
        }
    }

    @Override
    public void onPageSelected(final int position) {
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
            if (startX == Integer.MIN_VALUE) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        onPageManuallyChanged(position);
                    }
                });
            } else {
                onPageManuallyChanged(position);
            }
        }
    }

    private void onPageManuallyChanged(int position) {
        currentPage = position;
        int oldSurfaceStart = surfaceStart;
        int oldSurfaceEnd = surfaceEnd;
        correctSurface();
        correctStartXOnPageManuallyChanged(oldSurfaceStart, oldSurfaceEnd);
        invalidate();
    }

    private void correctStartXOnPageManuallyChanged(int oldSurfaceStart, int oldSurfaceEnd) {
        if (currentPage >= oldSurfaceStart && currentPage <= oldSurfaceEnd) {
            // startX is not changed
            return;
        }
        int corrected = startX;
        if (currentPage < oldSurfaceStart) {
            corrected += (oldSurfaceStart - currentPage) * (marginBetweenCircles + radius * 2);
        } else {
            corrected -= (currentPage - oldSurfaceEnd) * (marginBetweenCircles + radius * 2);
        }
        startX = corrected;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        measureStartX();
    }

    private void measureStartX() {
        if (startX == Integer.MIN_VALUE) {
            startX = getInitialStartX();
        }
    }

    private int getInitialStartX() {
        int result = getInternalPaddingLeft();

        if (getCount() <= onSurfaceCount) {
            return result;
        }
        int risingCount = getInternalRisingCount();
        if (risingCount == 0) {
            return result;
        }
        return (int) (result + risingCount * radius * 2 + (risingCount - 1) * marginBetweenCircles);
    }

    private int getInternalRisingCount() {
        int risingCount;
        if (getCount() < onSurfaceCount + this.risingCount) {
            risingCount = getCount() - onSurfaceCount;
        } else {
            risingCount = this.risingCount;
        }
        return risingCount;
    }

    private int getInternalPaddingLeft() {
        return (int) (getPaddingLeft() + radius);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (viewPager == null)) {
            //We were told how big to be
            result = specSize;
        } else {
            //Calculate the width according the views count
            result = calculateExactWidth();
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int calculateExactWidth() {
        int maxSurfaceCount = Math.min(getCount(), onSurfaceCount);
        int risingCount = getInternalRisingCount();
        int result = (int) (getPaddingLeft() + getPaddingRight()
                + maxSurfaceCount * 2 * radius + (maxSurfaceCount - 1) * marginBetweenCircles);
        if (risingCount > 0) {
            result += risingCount * radius * 2 + (risingCount - 1) * marginBetweenCircles
                    + getInitialStartX() - getInternalPaddingLeft();
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            result = specSize;
        } else {
            //Measure the height
            result = (int) (2 * (radius + scaleRadiusCorrection) + getPaddingTop() + getPaddingBottom());
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}