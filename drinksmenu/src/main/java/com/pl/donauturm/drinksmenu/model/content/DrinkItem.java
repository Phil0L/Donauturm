package com.pl.donauturm.drinksmenu.model.content;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@SuppressWarnings("unused")
public class DrinkItem extends DrinksMenuItem implements Serializable, Backgroundable, Drinktextable, Cloneable {

    private String drinkId;
    private String description;
    private BigDecimal price;
    private int backColor;
    private int cornerRadius;
    private DrinkStyle drinkStyle;

    public DrinkItem() {
    }

    public DrinkItem(Drink drink){
        this.drinkId = drink.getId();
        this.name = drink.name;
        this.description = drink.description;
        this.price = drink.price;
        this.drinkStyle = new DrinkStyle();
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
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

    public DrinkStyle getDrinkStyle() {
        return drinkStyle;
    }

    public void setDrinkStyle(DrinkStyle drinkStyle) {
        this.drinkStyle = drinkStyle;
    }

    @Override
    public int getNameColor() {
        return drinkStyle.getNameColor();
    }

    @Override
    public void setNameColor(int color) {
        drinkStyle.setNameColor(color);
    }

    @Override
    public int getNameFontSize() {
        return drinkStyle.getNameFontSize();
    }

    @Override
    public void setNameFontSize(int size) {
        drinkStyle.setNameFontSize(size);
    }

    @Override
    public Font getNameFont() {
        return drinkStyle.getNameFont();
    }

    @Override
    public void setNameFont(Font font) {
        drinkStyle.setNameFont(font);
    }

    @Override
    public int getDescriptionColor() {
        return drinkStyle.getDescriptionColor();
    }

    @Override
    public void setDescriptionColor(int color) {
        drinkStyle.setDescriptionColor(color);
    }

    @Override
    public int getDescriptionFontSize() {
        return drinkStyle.getDescriptionFontSize();
    }

    @Override
    public void setDescriptionFontSize(int size) {
        drinkStyle.setDescriptionFontSize(size);
    }

    @Override
    public Font getDescriptionFont() {
        return drinkStyle.getDescriptionFont();
    }

    @Override
    public void setDescriptionFont(Font font) {
        drinkStyle.setDescriptionFont(font);
    }

    @Override
    public boolean isPriceVisible() {
        return drinkStyle.isPriceVisible();
    }

    @Override
    public void setPriceVisible(boolean priceVisible) {
        drinkStyle.setPriceVisible(priceVisible);
    }

    @Override
    public int getPriceColor() {
        return drinkStyle.getPriceColor();
    }

    @Override
    public void setPriceColor(int color) {
        drinkStyle.setPriceColor(color);
    }

    @Override
    public int getPriceFontSize() {
        return drinkStyle.getPriceFontSize();
    }

    @Override
    public void setPriceFontSize(int size) {
        drinkStyle.setPriceFontSize(size);
    }

    @Override
    public Font getPriceFont() {
        return drinkStyle.getPriceFont();
    }

    @Override
    public void setPriceFont(Font font) {
        drinkStyle.setPriceFont(font);
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
        return Objects.hash(super.hashCode(), description, price, backColor, cornerRadius, drinkStyle);
    }

    @NonNull
    @Override
    public String toString() {
        return "Drink{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", backColor=" + backColor +
                ", cornerRadius=" + cornerRadius +
                ", drinkStyle=" + drinkStyle +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @NonNull
    @Override
    @SuppressWarnings("UnusedAssignment")
    public DrinkItem clone() {
        DrinkItem clone = (DrinkItem) super.clone();
        Gson gson = new Gson();
        String content = gson.toJson(this);
        clone = gson.fromJson(content, DrinkItem.class);
        clone.createNewId();
        return clone;
    }
}
