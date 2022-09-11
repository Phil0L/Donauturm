package com.pl.donauturm.drinksmenu.model.content;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;

import java.io.Serializable;
import java.util.Objects;

public class ShapeItem extends DrinksMenuItem implements Serializable, Backgroundable, Cloneable {

    private int backColor;
    private int cornerRadius;

    public ShapeItem() {
        super("Shape");
        this.backColor = Color.WHITE;
        this.cornerRadius = 10;
    }

    public ShapeItem(String name, int backColor, int cornerRadius) {
        super(name);
        this.backColor = backColor;
        this.cornerRadius = cornerRadius;
    }

    public ShapeItem(float left, float top, float width, float height) {
        super(left, top, width, height);
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
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), backColor, cornerRadius);
    }

    @NonNull
    @Override
    public String toString() {
        return "Shape{" +
                "name='" + name + '\'' +
                ", backColor=" + backColor +
                ", cornerRadius=" + cornerRadius +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @NonNull
    @Override
    @SuppressWarnings("UnusedAssignment")
    public ShapeItem clone() {
        ShapeItem clone = (ShapeItem) super.clone();
        Gson gson = new Gson();
        String content = gson.toJson(this);
        clone = gson.fromJson(content, ShapeItem.class);
        clone.createNewId();
        return clone;
    }
}
