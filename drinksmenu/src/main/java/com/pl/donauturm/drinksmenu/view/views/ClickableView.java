package com.pl.donauturm.drinksmenu.view.views;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class ClickableView implements View.OnTouchListener{

    private final ViewBuilder caller;

    private long clickStartTime;
    private boolean doClick = true;
    private float[] clickStartPos = new float[2];
    private int[] clickStartViewPos = new int[2];
    private static final int maxClickTime = 150; // ms
    private static final int maxMovement = 10; // px

    public ClickableView(ViewBuilder caller) {
        this.caller = caller;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!doClick) return false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                clickStartViewPos[0] = lParams.leftMargin;
                clickStartViewPos[1] = lParams.topMargin;
                clickStartPos[0] = event.getX();
                clickStartPos[1] = event.getY();
                clickStartTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_UP:
                int clickDuration = (int) (event.getEventTime() - clickStartTime);
                int xMovement = (int) (event.getX() - clickStartPos[0]);
                int yMovement = (int) (event.getY() - clickStartPos[1]);
                if (clickDuration <= maxClickTime && xMovement <= maxMovement && yMovement <= maxMovement) {
                    v.invalidate();
                    caller.onClick();
                    FrameLayout.LayoutParams nlParams = (FrameLayout.LayoutParams) caller.getLayoutParams();
                    nlParams.leftMargin = clickStartViewPos[0];
                    nlParams.topMargin = clickStartViewPos[1];
                    caller.setLayoutParams(nlParams);
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    protected void activateClick(){
        doClick = true;
    }

    protected void deactivateClick(){
        doClick = false;
    }

}
