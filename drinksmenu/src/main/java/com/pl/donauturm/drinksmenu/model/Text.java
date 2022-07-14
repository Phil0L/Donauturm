package com.pl.donauturm.drinksmenu.model;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Textable;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class Text extends Item implements Serializable, Backgroundable, Textable, Cloneable {

    private int backColor;
    private int cornerRadius;
    private int textColor;
    private int textSize;
    @Nullable
    private String fontPath;
    @Nullable
    private Font tempFont;

    public Text(String name) {
        super(name);
        this.textSize = 10;
        this.textColor = Color.WHITE;
        this.fontPath = null;
        this.tempFont = null;
    }

    @Override
    public int getBackgroundColor() {
        return backColor;
    }

    @Override
    public void setBackgroundColor(int color) {
        this.backColor = color;
    }

    @Override
    public int getCornerRadius() {
        return cornerRadius;
    }

    @Override
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
    }

    @Override
    public int getColor() {
        return textColor;
    }

    @Override
    public void setColor(int color) {
        this.textColor = color;
    }

    @Override
    public int getFontSize() {
        return textSize;
    }

    @Override
    public void setFontSize(int size) {
        this.textSize = size;
    }

    @Override
    @Nullable
    public Font getFont() {
        if (tempFont != null) return tempFont;
        if (fontPath == null) return null;
        return new Font(new File(fontPath));
    }

    @Override
    public void setFont(Font font) {
        this.tempFont = font;
        this.fontPath = font.getFile().getAbsolutePath();
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), backColor, cornerRadius, textColor, textSize, fontPath);
    }

    @NonNull
    @Override
    public String toString() {
        return "Text{" +
                "name='" + name + '\'' +
                ", backColor=" + backColor +
                ", cornerRadius=" + cornerRadius +
                ", textColor=" + textColor +
                ", textSize=" + textSize +
                ", fontPath='" + fontPath + '\'' +
                ", tempFont=" + tempFont +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @NonNull
    @Override
    @SuppressWarnings("UnusedAssignment")
    public Text clone() {
        Text clone = (Text) super.clone();
        Gson gson = new Gson();
        String content = gson.toJson(this);
        clone = gson.fromJson(content, Text.class);
        return clone;
    }
}
