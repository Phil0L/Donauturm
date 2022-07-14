package com.pl.donauturm.drinksmenu.view.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class ShapeView extends ItemView{

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.resizableHandler.setResizeDimensions(1, 1, 2000, 2000);
    }
}
