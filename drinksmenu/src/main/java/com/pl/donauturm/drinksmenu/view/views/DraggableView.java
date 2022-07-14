package com.pl.donauturm.drinksmenu.view.views;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;

public class DraggableView implements View.OnTouchListener {

    private final ViewBuilder caller;

    private int xDelta;
    private int yDelta;
    private boolean doDrag = true;
    public int minLeft, minTop, maxLeft, maxTop;

    public DraggableView(ViewBuilder caller) {
        this.caller = caller;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    @CallSuper
    public boolean onTouch(View v, MotionEvent event) {
        if (!doDrag) return false;
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                xDelta = X - lParams.leftMargin;
                yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                if (!doDrag) break;
                FrameLayout.LayoutParams layParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                caller.onDragged(
                        DrinksMenuEditorActivity.ValueScale.with(caller.getContext()).scaleViewToPosition(layParams.leftMargin),
                        DrinksMenuEditorActivity.ValueScale.with(caller.getContext()).scaleViewToPosition(layParams.topMargin));
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (!doDrag) break;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                layoutParams.leftMargin = Math.min(Math.max(X - xDelta, minLeft), maxLeft);
                layoutParams.topMargin = Math.min(Math.max(Y - yDelta, minTop), maxTop);
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                caller.setLayoutParams(layoutParams);
                break;
        }
        return true;
    }

    protected void activateDrag(){
        doDrag = true;
    }

    protected void deactivateDrag(){
        doDrag = false;
    }

    protected void setDraggableDimensions(int minL, int minT, int maxL, int maxT){
        this.minLeft = minL;
        this.minTop = minT;
        this.maxLeft = maxL;
        this.maxTop = maxT;
    }

}
