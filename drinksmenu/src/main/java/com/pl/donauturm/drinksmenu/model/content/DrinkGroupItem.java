package com.pl.donauturm.drinksmenu.model.content;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;
import com.pl.donauturm.drinksmenu.model.interfaces.Group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DrinkGroupItem extends DrinksMenuItem implements Group<DrinkItem>, Serializable, Backgroundable, Drinktextable, Cloneable {

    private List<DrinkItem> drinkItems;
    private DrinkStyle drinkStyle;
    private int backColor;
    private int cornerRadius;
    private int columnCount;
    private int columnSpacing;
    private int rowSpacing;

    public DrinkGroupItem() {
        super("Drink Group");
        this.drinkItems = new ArrayList<>();
        this.drinkStyle = new DrinkStyle();
    }

    public DrinkGroupItem(String name, List<DrinkItem> drinkItems, DrinkStyle drinkStyle) {
        super(name);
        this.drinkStyle = drinkStyle;
        this.drinkItems = new ArrayList<>(drinkItems);
    }

    public DrinkGroupItem(float left, float top, float width, float height) {
        super(left, top, width, height);
        setName("Item Group");
        this.drinkItems = new ArrayList<>();
    }

    @Override
    public List<DrinkItem> getItems() {
        return drinkItems;
    }

    public void setDrinks(List<DrinkItem> drinkItems) {
        this.drinkItems = drinkItems;
    }

    public void addDrinks(DrinkItem... drinkItems) {
        this.drinkItems.addAll(Arrays.asList(drinkItems));
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

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getColumnSpacing() {
        return columnSpacing;
    }

    public void setColumnSpacing(int columnSpacing) {
        this.columnSpacing = columnSpacing;
    }

    public int getRowSpacing() {
        return rowSpacing;
    }

    public void setRowSpacing(int rowSpacing) {
        this.rowSpacing = rowSpacing;
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
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), drinkItems, drinkStyle, backColor, cornerRadius, columnCount, columnSpacing, rowSpacing);
    }

    @NonNull
    @Override
    public String toString() {
        return "DrinkGroup{" +
                "name='" + name + '\'' +
                ", drinkStyle=" + drinkStyle +
                ", backColor=" + backColor +
                ", cornerRadius=" + cornerRadius +
                ", columnCount=" + columnCount +
                ", columnSpacing=" + columnSpacing +
                ", rowSpacing=" + rowSpacing +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", drinks=" + drinkItems +
                '}';
    }

    @NonNull
    @Override
    @SuppressWarnings("UnusedAssignment")
    public DrinkGroupItem clone() {
        DrinkGroupItem clone = (DrinkGroupItem) super.clone();
        Gson gson = new Gson();
        String content = gson.toJson(this);
        clone = gson.fromJson(content, DrinkGroupItem.class);
        clone.createNewId();
        return clone;
    }
}
