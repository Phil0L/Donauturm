package com.pl.donauturm.drinksmenu.view.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.util.DrinksMenuCanvas;
import com.pl.donauturm.drinksmenu.view.views.ViewBuilder;

import java.util.ArrayList;
import java.util.List;

public class PreviewHolder extends FrameLayout {

    @Deprecated
    public List<Canvas> noGuideCanvases = new ArrayList<>();

    public PreviewHolder(@NonNull Context context) {
        super(context);
    }

    public PreviewHolder(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewHolder(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreviewHolder(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * @deprecated Use {@link #draw(Canvas)} with a {@link DrinksMenuCanvas}
     */
    @Deprecated
    public void drawNoGuides(Canvas canvas){
        noGuideCanvases.add(canvas);
        super.draw(canvas);
        noGuideCanvases.remove(canvas);
    }

    @Deprecated
    protected boolean OLDdrawChild(Canvas canvas, View child, long drawingTime) {
        return drawChildSynchronous(canvas, child, drawingTime, !noGuideCanvases.contains(canvas));
    }

    @Deprecated
    protected synchronized boolean drawChildSynchronous(Canvas canvas, View child, long drawingTime, boolean showGuides) {
        if (child instanceof ViewBuilder)
            if (showGuides) ((ViewBuilder) child).showGuides();
            else ((ViewBuilder) child).hideGuides();
        boolean out = super.drawChild(canvas, child, drawingTime);
        if (child instanceof ViewBuilder)
            ((ViewBuilder) child).showGuides();
        return out;
    }
}
