
package kilfat.android_vk.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import kilfat.android_vk.R;

public class VerticalLinearLayout extends ViewGroup{

    private boolean mReverse;
    private int mSkipSinceChildIndexIncluding;

    public VerticalLinearLayout(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(true);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VLL_VerticalLinearLayout);
            setReverse(a.getBoolean(R.styleable.VLL_VerticalLinearLayout_reverse, false));
            a.recycle();
        } else {
            setReverse(false);
        }
    }

    public void setReverse(boolean reverse) {
        if (mReverse != reverse) {
            mReverse = reverse;
            requestLayout();
        }
    }


    // ============================================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // If parent would provide us a known width or height and if one them is 0-size
        // than we should skip all measure stuff, because we will not show anything
        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize == 0
                || heightMode != MeasureSpec.UNSPECIFIED && heightSize == 0) {
            setMeasuredDimension(widthSize, heightSize);
            return;
        }

        // We should do not care about widthMode for children, because of them will match w/ parent
        // But we should care about height of children

        // Here you can reserve any width or height for your own needs
        int widthUsedByParent = 0;
        int heightUsedByParent = 0;

        // Measure children
        int childrenMaxWidth = 0;
        int childrenAllHeight = 0;

        mSkipSinceChildIndexIncluding = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                // Implements all calculations w/ this view paddings & child layout_margin_*
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                measureChildWithMargins(
                        v,
                        widthMeasureSpec,
                        widthUsedByParent,
                        heightMeasureSpec,
                        heightUsedByParent);
                int childWidth = v.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int childHeight = v.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                if (childWidth > childrenMaxWidth) {
                    childrenMaxWidth = childWidth;
                }
                childrenAllHeight += childHeight;
            }

            mSkipSinceChildIndexIncluding++;
            if (heightMode != MeasureSpec.UNSPECIFIED
                    && heightUsedByParent + childrenAllHeight > heightSize) {
                // Do not measure views, that do not fit into given height
                break;
            }
        }

        // We should re-check all resulting sizes,
        // because any buggy-view can return invalid results and we will go out of available size
        setMeasuredDimension(
                getMeasurement(
                        widthMeasureSpec,
                        childrenMaxWidth + widthUsedByParent),
                getMeasurement(
                        heightMeasureSpec,
                        childrenAllHeight + heightUsedByParent)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mReverse) {
            layoutReverse(l, t, r, b);
        } else {
            layoutNormal(l, t, r, b);
        }
    }

    protected void layoutNormal(int l, int t, int r, int b) {
        // For children all layout are starts from 0 and l,t,r,b may be ignored
        int offsetLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        int offsetRight = getMeasuredWidth() - getPaddingRight();
        int left, top, right, bottom;

        int childCount = getChildCount();
        for (int i = 0; i < childCount && i < mSkipSinceChildIndexIncluding; i++) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                left = offsetLeft + lp.leftMargin;
                top = offsetTop + lp.topMargin;
                right = offsetRight - lp.rightMargin;
                bottom = top + v.getMeasuredHeight();
                layoutViewWithGravityInRow(v, left, top, right, bottom);
                offsetTop = bottom + lp.bottomMargin;
            }
        }
    }

    protected void layoutReverse(int l, int t, int r, int b) {
        // For children all layout are starts from 0 and l,t,r,b may be ignored
        int offsetLeft = getPaddingLeft();
        int offsetRight = getMeasuredWidth() - getPaddingRight();
        int offsetBottom = getMeasuredHeight() - getPaddingBottom();
        int left, top, right, bottom;

        int childCount = getChildCount();
        for (int i = 0; i < childCount && i < mSkipSinceChildIndexIncluding; i++) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                left = offsetLeft + lp.leftMargin;
                top = offsetBottom - lp.bottomMargin - v.getMeasuredHeight();
                right = offsetRight - lp.rightMargin;
                bottom = top + v.getMeasuredHeight();
                layoutViewWithGravityInRow(v, left, top, right, bottom);
                offsetBottom = top - lp.topMargin;
            }
        }
    }

    protected void layoutViewWithGravityInRow(View v, int l, int t, int r, int b) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        int parentWidth = r - l;
        int left, top, right, bottom;
        switch (lp.gravity) {
            case Gravity.CENTER:
            case Gravity.CENTER_HORIZONTAL:
                left = (parentWidth - v.getMeasuredWidth()) / 2;
                top = t;
                right = left + v.getMeasuredWidth();
                bottom = top + v.getMeasuredHeight();
                break;
            case Gravity.RIGHT:
                left = r - v.getMeasuredWidth();
                top = t;
                right = left + v.getMeasuredWidth();
                bottom = top + v.getMeasuredHeight();
                break;
            case Gravity.LEFT:
            case Gravity.NO_GRAVITY:
            default:
                left = l;
                top = t;
                right = left + v.getMeasuredWidth();
                bottom = top + v.getMeasuredHeight();
        }
        v.layout(left, top, right, bottom);
    }


    // ============================================================

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new VerticalLinearLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof VerticalLinearLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /**
     * Custom layout params
     */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public int gravity;

        /**
         * Creates a new set of layout parameters.
         * @param c The application environment
         * @param attrs The set of attributes fom which to extract the layout  parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            if (attrs != null) {
                TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.VLL_VerticalLinearLayout);
                gravity = a.getInt(R.styleable.VLL_VerticalLinearLayout_layout_gravity, Gravity.NO_GRAVITY);
                a.recycle();
            } else {
                gravity = Gravity.NO_GRAVITY;
            }
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


    // ============================================================

    /**
     * Utility to return a view's standard measurement. Uses the
     * supplied size when constraints are given. Attempts to
     * hold to the desired size unless it conflicts with provided
     * constraints.
     *
     * @param measureSpec Constraints imposed by the parent
     * @param contentSize Desired size for the view
     * @return The size the view should be.
     */
    protected static int getMeasurement(int measureSpec, int contentSize) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                //Big as we want to be
                return contentSize;
            case View.MeasureSpec.AT_MOST:
                //Big as we want to be, up to the spec
                return Math.min(contentSize, specSize);
            case View.MeasureSpec.EXACTLY:
                //Must be the spec size
                return specSize;
            default:
                return 0;
        }
    }
}
