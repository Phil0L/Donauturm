package com.pl.donauturm.drinksmenu.view.views;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;

public class ResizableView implements View.OnTouchListener{

    private final ViewBuilder caller;

    private int xDelta;
    private int yDelta;
    private boolean activeResize;
    private boolean deacivatedDrag;
    private boolean doResize = true;
    public int minWidth, minHeight, maxWidth, maxHeight;

    public ResizableView(ViewBuilder caller) {
        this.caller = caller;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                final int size = 50;
                final int[] screenPosition = new int[2];
                caller.getLocationOnScreen(screenPosition);
                final int right = screenPosition[0] + caller.getWidth();
                final int bottom = screenPosition[1] + caller.getHeight();
                if (X < right - size || Y < bottom - size){
                    // don't scale
                    activeResize = false;
                    break;
                } else {
                    // scale
                    activeResize = true;
                    if (caller.isDraggable()) {
                        caller.deactivateDrag();
                        deacivatedDrag = true;
                    }
                }

                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                xDelta = X - lParams.width;
                yDelta = Y - lParams.height;
                return true;
            case MotionEvent.ACTION_UP:
                if (!doResize) break;
                FrameLayout.LayoutParams layParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                boolean returnTrue = false;
                if (activeResize) {
                    caller.onResize(
                            DrinksMenuEditorActivity.ValueScale.with(caller.getContext()).scaleViewToPosition(layParams.width),
                            DrinksMenuEditorActivity.ValueScale.with(caller.getContext()).scaleViewToPosition(layParams.height));
                    returnTrue = true;
                }
                activeResize = false;
                if (deacivatedDrag) {
                    caller.activateDrag();
                    deacivatedDrag = false;
                }
                return returnTrue;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (!activeResize) break;
                if (!doResize) break;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                layoutParams.width = Math.min(Math.max((X - xDelta), minWidth), maxWidth);
                layoutParams.height = Math.min(Math.max((Y - yDelta), minHeight), maxHeight);
                caller.setLayoutParams(layoutParams);
                break;
        }
        return false;
    }

    protected void activateResize(){
        doResize = true;
    }

    protected void deactivateResize(){
        doResize = false;
    }

    protected void setResizeDimensions(int minW, int minH, int maxW, int maxH){
        this.minWidth = minW;
        this.minHeight = minH;
        this.maxWidth = maxW;
        this.maxHeight = maxH;
    }

}
