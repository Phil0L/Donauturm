package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.DrinkGroupBottomSheet;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;

import java.util.Collections;
import java.util.List;

public class DrinkGroupEventHandler extends ItemEventHandler implements DrinkGroupBottomSheet.DrinkGroupEvent {

    public DrinkGroupEventHandler(EditorProvider provider) {
        super(provider);
    }

    @Override
    public void onItemAdded(int index, DrinksMenuItem item) {
        DrinksMenuItem itemG = provider.getCurrentItem();
        if (itemG instanceof DrinkGroupItem && item instanceof DrinkItem) {
            DrinkGroupItem drinkGroupItem = ((DrinkGroupItem) itemG);
            DrinkItem drinkItem = (DrinkItem) item;
            drinkGroupItem.addDrinks(drinkItem);
        }
    }

    @Override
    public void onItemMoved(int from, int to) {
        DrinksMenuItem itemG = provider.getCurrentItem();
        if (itemG instanceof DrinkGroupItem) {
            DrinkGroupItem drinkGroupItem = ((DrinkGroupItem) itemG);
            List<DrinkItem> drinkItems = drinkGroupItem.getItems();
            Collections.swap(drinkItems, from, to);
            drinkGroupItem.setDrinks(drinkItems);
        }
    }

    @Override
    public void onItemDelete(int index) {
        DrinksMenuItem itemG = provider.getCurrentItem();
        if (itemG instanceof DrinkGroupItem) {
            DrinkGroupItem drinkGroupItem = ((DrinkGroupItem) itemG);
            List<DrinkItem> drinkItems = drinkGroupItem.getItems();
            drinkItems.remove(index);
            drinkGroupItem.setDrinks(drinkItems);
        }
    }

    @Override
    public void onItemsChanged(List<? extends DrinksMenuItem> items) {
        onAnyChange();
        if (provider.getBottomSheet() != null)
            provider.getBottomSheet().update();
        if (provider.getSelectedView() != null)
            provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onBackgroundChanged(int color) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Backgroundable)
            ((Backgroundable) item).setBackgroundColor(color);
        if (provider.getSelectedView() != null)
            provider.getSelectedView().setBackgroundColor(color);
    }

    @Override
    public void onCornerRadiusChanged(int radius) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Backgroundable)
            ((Backgroundable) item).setCornerRadius(radius);
        if (provider.getSelectedView() != null)
            provider.getSelectedView().setCornerRadius(radius);
    }

    @Override
    public void onNameColorChange(int color) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setNameColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onNameSizeChange(int size) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setNameFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onNameFontChange(Font font) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setNameFont(font);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onDescColorChange(int color) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setDescriptionColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onDescSizeChange(int size) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setDescriptionFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onDescFontChange(Font font) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setDescriptionFont(font);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceVisibleChange(boolean visible) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceVisible(visible);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceColorChange(int color) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceSizeChange(int size) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceFontChange(Font font) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceFont(font);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onColumnCountChange(int columns) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof DrinkGroupItem)
            ((DrinkGroupItem) item).setColumnCount(columns);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onColumnSpacingChange(int space) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof DrinkGroupItem)
            ((DrinkGroupItem) item).setColumnSpacing(space);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onRowSpacingChange(int space) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof DrinkGroupItem)
            ((DrinkGroupItem) item).setRowSpacing(space);
        provider.getSelectedView().notifyChanged();
    }
}
