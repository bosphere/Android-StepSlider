package com.bosphere.stepslider;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bo on 9/1/18.
 */

public class StepSlider extends View {

    private static final int THUMB_RADIUS_BG = 8;
    private static final int THUMB_RADIUS_FG = 6;

    private static final int TRACK_HEIGHT_BG = 4;
    private static final int TRACK_HEIGHT_FG = 2;

    private final int COLOR_BG = Color.parseColor("#dddfeb");
    private final int COLOR_FG = Color.parseColor("#7da1ae");

    private int mThumbBgRadius, mThumbFgRadius;
    private int mTrackBgHeight, mTrackFgHeight;
    private int mNumStep = 3;
    private Paint mThumbBgPaint, mThumbFgPaint;
    private Paint mTrackBgPaint, mTrackFgPaint;
    private RectF mTrackRect;
    private int mPosition;
    private OnSliderPositionChangeListener mListener;

    public StepSlider(Context context) {
        super(context);
        init(null, 0);
    }

    public StepSlider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StepSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void setStepCount(int steps) {
        mNumStep = steps;
        invalidate();
    }

    public void setThumbColor(@ColorInt int color) {
        mThumbFgPaint.setColor(color);
        invalidate();
    }

    public void setTrackColor(@ColorInt int color) {
        mTrackFgPaint.setColor(color);
        invalidate();
    }

    public void setThumbBgColor(@ColorInt int color) {
        mThumbBgPaint.setColor(color);
        invalidate();
    }

    public void setTrackBgColor(@ColorInt int color) {
        mTrackBgPaint.setColor(color);
        invalidate();
    }

    public void setThumbRadiusPx(int radiusPx) {
        mThumbFgRadius = radiusPx;
        invalidate();
    }

    public void setThumbBgRadiusPx(int radiusPx) {
        mThumbBgRadius = radiusPx;
        invalidate();
    }

    public void setTrackHeightPx(int heightPx) {
        mTrackFgHeight = heightPx;
        invalidate();
    }

    public void setTrackBgHeightPx(int heightPx) {
        mTrackBgHeight = heightPx;
        invalidate();
    }

    public void setPosition(int position) {
        onPositionChanged(position, false);
    }

    public void setOnSliderPositionChangeListener(OnSliderPositionChangeListener listener) {
        mListener = listener;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        // to support non-touchable environment
        setFocusable(true);

        int colorDefaultBg = resolveAttrColor("colorControlNormal", COLOR_BG);
        int colorDefaultFg = resolveAttrColor("colorControlActivated", COLOR_FG);

        mThumbBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbBgPaint.setStyle(Paint.Style.FILL);
        mThumbBgPaint.setColor(colorDefaultBg);

        mThumbFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbFgPaint.setStyle(Paint.Style.FILL);
        mThumbFgPaint.setColor(colorDefaultFg);

        mTrackBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackBgPaint.setStyle(Paint.Style.FILL);
        mTrackBgPaint.setColor(colorDefaultBg);

        mTrackFgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackFgPaint.setStyle(Paint.Style.FILL);
        mTrackFgPaint.setColor(colorDefaultFg);

        mTrackRect = new RectF();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mThumbBgRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMB_RADIUS_BG, dm);
        mThumbFgRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMB_RADIUS_FG, dm);
        mTrackBgHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TRACK_HEIGHT_BG, dm);
        mTrackFgHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TRACK_HEIGHT_FG, dm);

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.StepSlider, defStyleAttr, 0);
            int thumbColor = arr.getColor(R.styleable.StepSlider_ss_thumb_color, mThumbFgPaint.getColor());
            mThumbFgPaint.setColor(thumbColor);

            int thumbBgColor = arr.getColor(R.styleable.StepSlider_ss_thumb_bg_color, mThumbBgPaint.getColor());
            mThumbBgPaint.setColor(thumbBgColor);

            int trackColor = arr.getColor(R.styleable.StepSlider_ss_track_color, mTrackFgPaint.getColor());
            mTrackFgPaint.setColor(trackColor);

            int trackBgColor = arr.getColor(R.styleable.StepSlider_ss_track_bg_color, mTrackBgPaint.getColor());
            mTrackBgPaint.setColor(trackBgColor);

            mThumbFgRadius = arr.getDimensionPixelSize(R.styleable.StepSlider_ss_thumb_radius, mThumbFgRadius);
            mThumbBgRadius = arr.getDimensionPixelSize(R.styleable.StepSlider_ss_thumb_bg_radius, mThumbBgRadius);
            mTrackFgHeight = arr.getDimensionPixelSize(R.styleable.StepSlider_ss_track_height, mTrackFgHeight);
            mTrackBgHeight = arr.getDimensionPixelSize(R.styleable.StepSlider_ss_track_bg_height, mTrackBgHeight);

            mNumStep = arr.getInteger(R.styleable.StepSlider_ss_step, mNumStep);

            arr.recycle();
        }
    }

    @ColorInt
    private int resolveAttrColor(String attrName, @ColorInt int defaultColor) {
        String packageName = getContext().getPackageName();
        int attrRes = getResources().getIdentifier(attrName, "attr", packageName);
        if (attrRes <= 0) {
            return defaultColor;
        }
        TypedValue value = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(attrRes, value, true);
        return getResources().getColor(value.resourceId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        int contentHeight = getPaddingTop() + Math.max(mThumbBgRadius, mThumbFgRadius) * 2 + getPaddingBottom();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            height = Math.max(contentHeight, getSuggestedMinimumHeight());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || mNumStep <= 1) {
            return false;
        }

        float x = event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int maxRadius = Math.max(mThumbBgRadius, mThumbFgRadius);
                int width = getWidth() - getPaddingLeft() - getPaddingRight() - 2 * maxRadius;
                int stepSize = width / (mNumStep - 1);
                int left = 0, right;
                for (int i = 0; i < mNumStep; i++) {
                    right = getPaddingLeft() + maxRadius + stepSize * i;
                    if (left == 0) {
                        left = right;
                    }

                    if (x <= left) {
                        onPositionChanged(i, true);
                        break;
                    } else if (x <= right) {
                        if (x - left > right - x) {
                            onPositionChanged(i, true);
                        } else {
                            onPositionChanged(i - 1, true);
                        }
                        break;
                    } else if (i == mNumStep - 1) {
                        onPositionChanged(i, true);
                        break;
                    }
                    left = right;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mPosition > 0) {
                onPositionChanged(mPosition - 1, true);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mPosition < mNumStep - 1) {
                onPositionChanged(mPosition + 1, true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onPositionChanged(int pos, boolean notifyChange) {
        if (mPosition == pos) {
            return;
        }

        mPosition = pos;
        invalidate();

        if (notifyChange && mListener != null) {
            mListener.onPositionChanged(pos);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mNumStep <= 0) {
            return;
        }

        drawSlider(canvas, mThumbBgRadius, mThumbBgRadius - mThumbFgRadius, mTrackBgHeight, mThumbBgPaint, mTrackBgPaint, mNumStep - 1);
        drawSlider(canvas, mThumbFgRadius, mThumbFgRadius - mThumbBgRadius, mTrackFgHeight, mThumbFgPaint, mTrackFgPaint, mPosition);
    }

    private void drawSlider(Canvas canvas, int thumbRadius, int thumbRadiusDelta, int trackHeight, Paint thumbPaint,
            Paint trackPaint, int position) {

        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();
        if (thumbRadiusDelta < 0) {
            leftPadding -= thumbRadiusDelta;
            rightPadding -= thumbRadiusDelta;
        }
        int left = leftPadding;
        int top = getPaddingTop();
        int width = getWidth() - leftPadding - rightPadding;
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        int stepSize;
        if (mNumStep == 1) {
            // only one step, center it
            left += (width - thumbRadius * 2) >> 1;
            stepSize = 0;
        } else {
            stepSize = (width - thumbRadius * 2) / (mNumStep - 1);
        }

        if (mNumStep > 1 && position > 0) {
            int trackLeft = left + thumbRadius;
            int trackTop = top + ((height - trackHeight) >> 1);
            int trackRight = trackLeft + position * stepSize;
            int trackBottom = trackTop + trackHeight;
            float trackRadius = trackHeight * 0.5f;
            mTrackRect.set(trackLeft, trackTop, trackRight, trackBottom);
            canvas.drawRoundRect(mTrackRect, trackRadius, trackRadius, trackPaint);
        }

        int leftOffset, topOffset;
        for (int i = 0; i <= position; i++) {
            leftOffset = stepSize * i;
            // draw bg thumb
            topOffset = (height - thumbRadius * 2) >> 1;
            canvas.drawCircle(left + leftOffset + thumbRadius, top + topOffset + thumbRadius,
                    thumbRadius, thumbPaint);
        }
    }
}
