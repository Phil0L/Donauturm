package com.pl.donauturm.drinksmenu.view.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.util.DrinksMenuCanvas;

import java.util.List;

public abstract class ViewBuilder extends GridView implements View.OnTouchListener {

    private final boolean draggable, resizable, clickable, backgroundable;
    private boolean doDraggable, doResizable;
    protected DraggableView draggableHandler;
    protected ResizableView resizableHandler;
    protected ClickableView clickableHandler;

    private Drawable borderDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.vec_item_group_border, null);
    private Drawable circleDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.vec_item_group_circle, null);
    private Drawable iconDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_scale, null);
    private int backgroundColor = Color.TRANSPARENT;
    private int cornerRadius = 0;
    private boolean showGuides = true;
    private final Paint paint = new Paint();

    public ViewBuilder(Context context) {
        this(context, null);
    }

    public ViewBuilder(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewBuilder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        draggable = defineAbilities().contains(Abilities.DRAGGABLE);
        doDraggable = draggable;
        if (draggable) draggableHandler = new DraggableView(this);
        resizable = defineAbilities().contains(Abilities.RESIZEABLE);
        doResizable = resizable;
        if (resizable) resizableHandler = new ResizableView(this);
        clickable = defineAbilities().contains(Abilities.CLICKABLE);
        if (clickable) clickableHandler = new ClickableView(this);
        backgroundable = defineAbilities().contains(Abilities.BACKGROUNDABLE);
    }

    public abstract List<Abilities> defineAbilities();

    @Override
    public void draw(Canvas canvas) {
        showGuides = !(canvas instanceof DrinksMenuCanvas);

        // draw border
        int right = getWidth();
        int bottom = getHeight();
        int left = 0;
        int top = 0;

        if (showGuides && (doDraggable || doResizable)) {
            getBorderDrawable().setBounds(left, top, right, bottom);
            getBorderDrawable().draw(canvas);
        }

        if (isBackgroundable()) {
            // draw background
            int borderWidth = getResources().getDimensionPixelSize(R.dimen.item_group_border_width);
            right = getWidth() - borderWidth;
            bottom = getHeight() - borderWidth;
            left = borderWidth;
            top = borderWidth;

            paint.setColor(getBackgroundColor());
            canvas.drawRoundRect(left, top, right, bottom, getCornerRadius(), getCornerRadius(), paint);
        }

        if (isResizable()){
            // draw circle
            int iconWidth = 55;
            int offsetX = -20;
            int offsetY = -20;
            right = getWidth() - offsetX;
            bottom = getHeight() - offsetY;
            left = right - iconWidth + offsetX;
            top = bottom - iconWidth + offsetY;
            if (showGuides && doResizable) {
                getCircleDrawable().setBounds(left, top, right, bottom);
                getCircleDrawable().draw(canvas);
            }

            // draw icon
            iconWidth = 40;
            offsetX = 5;
            offsetY = 5;
            right = getWidth() - offsetX;
            bottom = getHeight() - offsetY;
            left = right - iconWidth + offsetX;
            top = bottom - iconWidth + offsetY;
            if (showGuides && doResizable) {
                getIconDrawable().setBounds(left, top, right, bottom);
                getIconDrawable().draw(canvas);
            }
        }
        try {
            super.draw(canvas);
        }catch (NullPointerException npe){
            Log.e(getClass().getSimpleName(), "Failed to draw GridView");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (clickable)
            if (!clickableHandler.onTouch(v, event))
                if (doResizable)
                    if (!resizableHandler.onTouch(v, event))
                        if (doDraggable)
                            draggableHandler.onTouch(v, event);
        getRootView().invalidate();
        v.invalidate();
        invalidate();
        return true;
    }

    public void onDragged(int newLeft, int newTop){

    }

    public void onResize(int newWidth, int newHeight){

    }

    public void onClick(){

    }

    public boolean isDraggable() {
        return doDraggable && draggableHandler != null;
    }

    public boolean isResizable() {
        return doResizable && resizableHandler != null;
    }

    public boolean isClickable() {
        return clickable && clickableHandler != null;
    }

    public boolean isBackgroundable() {
        return backgroundable;
    }

    public Drawable getBorderDrawable() {
        return borderDrawable;
    }

    protected void setBorderDrawable(Drawable borderDrawable) {
        this.borderDrawable = borderDrawable;
    }

    public Drawable getCircleDrawable() {
        return circleDrawable;
    }

    protected void setCircleDrawable(Drawable circleDrawable) {
        this.circleDrawable = circleDrawable;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    protected void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getCornerRadius(){
        return cornerRadius;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        super.invalidate();
    }

    public void setCornerRadius(int radius){
        this.cornerRadius = radius;
        super.invalidate();
    }

    public void deactivateDrag() {
        if (draggable && draggableHandler != null) {
            draggableHandler.deactivateDrag();
            doDraggable = false;
        }
    }

    public void activateDrag() {
        if (draggable && draggableHandler != null) {
            draggableHandler.activateDrag();
            doDraggable = true;
        }
    }

    public void deactivateResize(){
        if (resizable && resizableHandler != null) {
            resizableHandler.deactivateResize();
            doResizable = false;
        }
    }

    public void activateResize(){
        if (resizable && resizableHandler != null) {
            resizableHandler.activateResize();
            doResizable = true;
        }
    }

    public void deactivateClick(){
        if (clickable && clickableHandler != null)
            clickableHandler.deactivateClick();
    }

    public void activateClick(){
        if (clickable && clickableHandler != null)
            clickableHandler.activateClick();
    }

    public void hideGuides(){
        showGuides = false;
    }

    public void showGuides(){
        showGuides = true;
    }

    public enum Abilities{
        DRAGGABLE,
        RESIZEABLE,
        CLICKABLE,
        BACKGROUNDABLE
    }

}
