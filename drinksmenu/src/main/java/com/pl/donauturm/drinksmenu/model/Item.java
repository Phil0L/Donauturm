package com.pl.donauturm.drinksmenu.model;

import androidx.annotation.NonNull;

import com.pl.donauturm.drinksmenu.util.JsonSubtype;

import java.io.Serializable;
import java.util.Objects;

@JsonSubtype(field = "type", clazz = Text.class)
@JsonSubtype(field = "type", clazz = Shape.class)
@JsonSubtype(field = "type", clazz = Drink.class)
@JsonSubtype(field = "type", clazz = DrinkGroup.class)
public abstract class Item implements Serializable, Cloneable {

    private final String type;

    protected String name;

    protected float left;
    protected float top;
    protected float width;
    protected float height;

    private boolean sizeLocked;
    private boolean positionLocked;

    public Item() {
        this.left = 0;
        this.top = 0;
        this.width = 200;
        this.height = 200;
        this.sizeLocked = false;
        this.positionLocked = false;
        this.name = "Item";
        this.type = getClass().getSimpleName();
    }

    public Item(float left, float top, float width, float height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.sizeLocked = false;
        this.positionLocked = false;
        this.name = "Item";
        this.type = getClass().getSimpleName();
    }

    public Item(String name) {
        this.left = 0;
        this.top = 0;
        this.width = 200;
        this.height = 200;
        this.sizeLocked = false;
        this.positionLocked = false;
        this.name = name;
        this.type = getClass().getSimpleName();
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isSizeLocked() {
        return sizeLocked;
    }

    public void setSizeLocked(boolean sizeLocked) {
        this.sizeLocked = sizeLocked;
    }

    public boolean isPositionLocked() {
        return positionLocked;
    }

    public void setPositionLocked(boolean positionLocked) {
        this.positionLocked = positionLocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, left, top, width, height);
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @NonNull
    @Override
    public Item clone() {
        try {
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
