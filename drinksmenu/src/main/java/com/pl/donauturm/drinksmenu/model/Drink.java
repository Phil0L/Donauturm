package com.pl.donauturm.drinksmenu.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.interfaces.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Drink implements Serializable, Id, Cloneable {

    private String id;
    public String name;
    public String description;
    public BigDecimal price;

    public boolean crossedOut;
    public boolean hidden;

    @CreatorConstructor
    public Drink() {
        this.createNewId();
        this.name = "Drink";
        this.description = "This is a drink";
        this.price = new BigDecimal(0);
        this.crossedOut = false;
        this.hidden = false;
    }

    public Drink(String name, String description, BigDecimal price) {
        this.createNewId();
        this.name = name;
        this.description = description;
        this.price = price;
        this.crossedOut = false;
        this.hidden = false;
    }

    public Drink(String name, String description, int price) {
        this.createNewId();
        this.name = name;
        this.description = description;
        this.price = new BigDecimal(price);
        this.crossedOut = false;
        this.hidden = false;
    }

    public Drink(String name, String description, float price) {
        this.createNewId();
        this.name = name;
        this.description = description;
        this.price = new BigDecimal(price);
        this.crossedOut = false;
        this.hidden = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getIdLong() {
        return Long.parseLong(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        if (price.stripTrailingZeros().scale() <= 0)
            return price.intValue() + "€";
        return price.setScale(2, RoundingMode.HALF_UP).floatValue() + "€";
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isCrossedOut() {
        return crossedOut;
    }

    public void setCrossedOut(boolean crossedOut) {
        this.crossedOut = crossedOut;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public Id createNewId() {
        this.id = newId();
        return this;
    }

    @NonNull
    @Override
    @SuppressWarnings("UnusedAssignment")
    public Drink clone() {
        try {
            Drink clone = (Drink) super.clone();
            Gson gson = new Gson();
            String content = gson.toJson(this);
            clone = gson.fromJson(content, Drink.class);
            clone.createNewId();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
