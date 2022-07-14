package com.pl.donauturm.drinksmenu.model.interfaces;

import com.pl.donauturm.drinksmenu.model.Font;

public interface Drinktextable {

    int getNameColor();

    void setNameColor(int color);

    int getNameFontSize();

    void setNameFontSize(int size);

    Font getNameFont();

    void setNameFont(Font font);

    int getDescriptionColor();

    void setDescriptionColor(int color);

    int getDescriptionFontSize();

    void setDescriptionFontSize(int size);

    Font getDescriptionFont();

    void setDescriptionFont(Font font);

    boolean isPriceVisible();

    void setPriceVisible(boolean visible);

    int getPriceColor();

    void setPriceColor(int color);

    int getPriceFontSize();

    void setPriceFontSize(int size);

    Font getPriceFont();

    void setPriceFont(Font font);
}
