package com.pl.donauturm.drinksmenu.model;

import android.graphics.Color;

import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;

import java.io.File;
import java.io.Serializable;

public class DrinkStyle implements Serializable, Drinktextable {

    private int nameSize;
    private int nameColor;
    private String nameFont;
    private Font tempNameFont;
    private int descriptionSize;
    private int descriptionColor;
    private String descriptionFont;
    private Font tempDescriptionFont;
    private boolean priceVisible;
    private int priceSize;
    private int priceColor;
    private String priceFont;
    private Font tempPriceFont;

    public DrinkStyle() {
        this.nameSize = 10;
        this.nameColor = Color.WHITE;
        this.nameFont = null;
        this.tempNameFont = null;
        this.descriptionSize = 7;
        this.descriptionColor = Color.WHITE;
        this.descriptionFont = null;
        this.tempDescriptionFont = null;
        this.priceVisible = true;
        this.priceSize = 10;
        this.priceColor = Color.WHITE;
        this.priceFont = null;
        this.tempPriceFont = null;
    }

    @Override
    public int getNameFontSize() {
        return nameSize;
    }

    @Override
    public void setNameFontSize(int nameSize) {
        this.nameSize = nameSize;
    }

    @Override
    public Font getNameFont() {
        if (tempNameFont != null) return tempNameFont;
        if (nameFont == null) return null;
        return new Font(new File(nameFont));
    }

    @Override
    public void setNameFont(Font font) {
        this.tempNameFont = font;
        this.nameFont = font.getFile().getAbsolutePath();
    }

    @Override
    public int getNameColor() {
        return nameColor;
    }

    @Override
    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
    }

    @Override
    public int getDescriptionColor() {
        return descriptionColor;
    }

    @Override
    public void setDescriptionColor(int color) {
        this.descriptionColor = color;
    }

    @Override
    public int getDescriptionFontSize() {
        return descriptionSize;
    }

    @Override
    public void setDescriptionFontSize(int size) {
        this.descriptionSize = size;
    }

    @Override
    public Font getDescriptionFont() {
        if (tempDescriptionFont != null) return tempDescriptionFont;
        if (descriptionFont == null) return null;
        return new Font(new File(descriptionFont));
    }

    @Override
    public void setDescriptionFont(Font font) {
        this.tempDescriptionFont = font;
        this.descriptionFont = font.getFile().getAbsolutePath();
    }

    @Override
    public boolean isPriceVisible() {
        return priceVisible;
    }

    @Override
    public void setPriceVisible(boolean priceVisible) {
        this.priceVisible = priceVisible;
    }

    @Override
    public int getPriceColor() {
        return priceColor;
    }

    @Override
    public void setPriceColor(int color) {
        this.priceColor = color;
    }

    @Override
    public int getPriceFontSize() {
        return priceSize;
    }

    @Override
    public void setPriceFontSize(int size) {
        this.priceSize = size;
    }

    @Override
    public Font getPriceFont() {
        if (tempPriceFont != null) return tempPriceFont;
        if (priceFont == null) return null;
        return new Font(new File(priceFont));
    }

    @Override
    public void setPriceFont(Font font) {
        this.tempPriceFont = font;
        this.priceFont = font.getFile().getAbsolutePath();
    }
}
