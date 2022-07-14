package com.pl.donauturm.drinksmenu.model.interfaces;

import androidx.annotation.ColorInt;

import com.pl.donauturm.drinksmenu.model.Font;

public interface Textable {

    @ColorInt
    int getColor();

    void setColor(@ColorInt int color);

    int getFontSize();

    void setFontSize(int size);

    Font getFont();

    void setFont(Font font);

}
