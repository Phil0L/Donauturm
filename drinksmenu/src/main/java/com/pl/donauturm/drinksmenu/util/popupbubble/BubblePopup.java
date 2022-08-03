package com.pl.donauturm.drinksmenu.util.popupbubble;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class BubblePopup {

    public static void show(@NonNull Activity context, @NonNull BubbleLayout bubbleLayout, @NonNull View anchor) {
        if (bubbleLayout.getMeasuredWidth() == 0 || bubbleLayout.getMeasuredHeight() == 0) {
            bubbleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }
        final int winPadding = 10;
        final int[] location = new int[2];
        anchor.getLocationInWindow(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int winHeight = displayMetrics.heightPixels;
        int winWidth = displayMetrics.widthPixels;
        int left = location[0];
        int top = location[1];
        int width = anchor.getMeasuredWidth();
        int height = anchor.getMeasuredHeight();
        int centerX = left + width / 2;
        int centerY = top + height / 2;
        int bubbleWidth = bubbleLayout.getMeasuredWidth();
        int bubbleHeight = bubbleLayout.getMeasuredHeight();
        int arrowOffset = 0;
        int bubbleOffset;
        ArrowDirection arrowDirection = bubbleLayout.getArrowDirection();
        switch (arrowDirection) {
            case LEFT:
            case LEFT_CENTER:
                left += width;
                bubbleOffset = (height - bubbleHeight) / 2;
                top += bubbleOffset;
                top = max(top, winPadding);
                top = min(top, winHeight - bubbleHeight - winPadding);
                arrowOffset = centerY - top;
                break;
            case RIGHT:
            case RIGHT_CENTER:
                left -= bubbleWidth;
                bubbleOffset = (height - bubbleHeight) / 2;
                top += bubbleOffset;
                top = max(top, winPadding);
                top = min(top, winHeight - bubbleHeight - winPadding);
                arrowOffset = centerY - top;
                break;
            case TOP:
            case TOP_CENTER:
            case TOP_RIGHT:
                top += height;
                bubbleOffset = (width - bubbleWidth) / 2;
                left += bubbleOffset;
                left = max(left, winPadding);
                left = min(left, winWidth - bubbleWidth - winPadding);
                arrowOffset = centerX - left;
                break;
            case BOTTOM:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                top -= bubbleHeight;
                bubbleOffset = (width - bubbleWidth) / 2;
                left += bubbleOffset;
                left = max(left, winPadding);
                left = min(left, winWidth - bubbleWidth - winPadding);
                arrowOffset = centerX - left;
                break;
        }
        bubbleLayout.setArrowPosition(arrowOffset);
        PopupWindow popupWindow = create(context, bubbleLayout);
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, left, top);
    }

    public static void show(@NonNull Activity context, @NonNull BubbleLayout bubbleLayout, @NonNull View anchor, ArrowDirection arrowDirection) {
        bubbleLayout.setArrowDirection(arrowDirection);
        show(context, bubbleLayout, anchor);
    }

    private static PopupWindow create(@NonNull Context context, @NonNull BubbleLayout bubbleLayout) {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(bubbleLayout);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // change background color to transparent
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return popupWindow;
    }

    private final Activity context;
    private BubbleLayout bubbleLayout;

    private PopupWindow.OnDismissListener dismissListener;
    private View.OnClickListener clickListener;

    public BubblePopup(Activity context) {
        this.context = context;
    }

    protected abstract BubbleLayout onCreateView(@NonNull View anchor, @NonNull LayoutInflater inflater, @Nullable BubbleLayout cachedView);

    protected int getWindowPadding() {
        return 10;
    }

    protected boolean isOutsideTouchable() {
        return true;
    }

    protected Drawable getBackground() {
        return new ColorDrawable(Color.TRANSPARENT);
    }

    protected void setOnDismissListener(PopupWindow.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    protected void setOnClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    protected ArrowDirection forceArrowDirection() {
        return null;
    }

    public int getTopBoundary() {
        return getWindowPadding();
    }

    public int getLeftBoundary() {
        return getWindowPadding();
    }

    public int getRightBoundary() {
        return getWindowPadding();
    }

    public int getBottomBoundary() {
        return getWindowPadding();
    }

    public void show(View anchor) {
        bubbleLayout = onCreateView(anchor, LayoutInflater.from(context), this.bubbleLayout);
        _show(anchor);
    }

    private void _show(@NonNull View anchor) {
        if (forceArrowDirection() != null) {
            bubbleLayout.setArrowDirection(forceArrowDirection());
        }
        if (bubbleLayout.getMeasuredWidth() == 0 || bubbleLayout.getMeasuredHeight() == 0) {
            bubbleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }
        final int[] location = new int[2];
        anchor.getLocationInWindow(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int winHeight = displayMetrics.heightPixels;
        int winWidth = displayMetrics.widthPixels;
        int left = location[0];
        int top = location[1];
        int width = anchor.getMeasuredWidth();
        int height = anchor.getMeasuredHeight();
        int centerX = left + width / 2;
        int centerY = top + height / 2;
        int bubbleWidth = bubbleLayout.getMeasuredWidth();
        int bubbleHeight = bubbleLayout.getMeasuredHeight();
        int arrowOffset = 0;
        int bubbleOffset;
        ArrowDirection arrowDirection = bubbleLayout.getArrowDirection();
        switch (arrowDirection) {
            case LEFT:
            case LEFT_CENTER:
                left += width;
                bubbleOffset = (height - bubbleHeight) / 2;
                top += bubbleOffset;
                top = max(top, getTopBoundary());
                top = min(top, winHeight - bubbleHeight - getBottomBoundary());
                arrowOffset = centerY - top;
                break;
            case RIGHT:
            case RIGHT_CENTER:
                left -= bubbleWidth;
                bubbleOffset = (height - bubbleHeight) / 2;
                top += bubbleOffset;
                top = max(top, getTopBoundary());
                top = min(top, winHeight - bubbleHeight - getBottomBoundary());
                arrowOffset = centerY - top;
                break;
            case TOP:
            case TOP_CENTER:
            case TOP_RIGHT:
                top += height;
                bubbleOffset = (width - bubbleWidth) / 2;
                left += bubbleOffset;
                left = max(left, getLeftBoundary());
                left = min(left, winWidth - bubbleWidth - getRightBoundary());
                arrowOffset = centerX - left;
                break;
            case BOTTOM:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                top -= bubbleHeight;
                bubbleOffset = (width - bubbleWidth) / 2;
                left += bubbleOffset;
                left = max(left, getLeftBoundary());
                left = min(left, winWidth - bubbleWidth - getRightBoundary());
                arrowOffset = centerX - left;
                break;
        }
        bubbleLayout.setArrowPosition(arrowOffset);
        if (clickListener != null)
            bubbleLayout.setOnClickListener(clickListener);
        PopupWindow popupWindow = _create();
        if (dismissListener != null)
            popupWindow.setOnDismissListener(dismissListener);
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, left, top);
    }

    private PopupWindow _create() {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(bubbleLayout);
        popupWindow.setOutsideTouchable(isOutsideTouchable());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // change background color to transparent
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return popupWindow;
    }
}
