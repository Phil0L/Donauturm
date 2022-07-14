package com.pl.donauturm.drinksmenu.model.interfaces;

import androidx.annotation.ColorInt;

public interface Backgroundable {

    @ColorInt
    int getBackgroundColor();

    void setBackgroundColor(@ColorInt int color);

    int getCornerRadius();

    void setCornerRadius(int radius);

}
