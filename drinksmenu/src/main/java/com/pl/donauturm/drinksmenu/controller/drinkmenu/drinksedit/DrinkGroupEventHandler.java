package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.DrinkGroupBottomSheet;
import com.pl.donauturm.drinksmenu.model.content.Drink;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;

import java.util.Collections;
import java.util.List;

public class DrinkGroupEventHandler extends ItemEventHandler implements DrinkGroupBottomSheet.DrinkGroupEvent {

    public DrinkGroupEventHandler(EditorProvider provider) {
        super(provider);
    }

    @Override
    public void onItemAdded(int index, Item item) {
        Item itemG = provider.getCurrentItem();
        if (itemG instanceof DrinkGroup && item instanceof Drink) {
            DrinkGroup drinkGroup = ((DrinkGroup) itemG);
            Drink drink = (Drink) item;
            drinkGroup.addDrinks(drink);
        }
    }

    @Override
    public void onItemMoved(int from, int to) {
        Item itemG = provider.getCurrentItem();
        if (itemG instanceof DrinkGroup) {
            DrinkGroup drinkGroup = ((DrinkGroup) itemG);
            List<Drink> drinks = drinkGroup.getItems();
            Collections.swap(drinks, from, to);
            drinkGroup.setDrinks(drinks);
        }
    }

    @Override
    public void onItemDelete(int index) {
        Item itemG = provider.getCurrentItem();
        if (itemG instanceof DrinkGroup) {
            DrinkGroup drinkGroup = ((DrinkGroup) itemG);
            List<Drink> drinks = drinkGroup.getItems();
            drinks.remove(index);
            drinkGroup.setDrinks(drinks);
        }
    }

    @Override
    public void onItemsChanged(List<? extends Item> items) {
        onAnyChange();
        if (provider.getBottomSheet() != null)
            provider.getBottomSheet().update();
        if (provider.getSelectedView() != null)
            provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onBackgroundChanged(int color) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Backgroundable)
            ((Backgroundable) item).setBackgroundColor(color);
        if (provider.getSelectedView() != null)
            provider.getSelectedView().setBackgroundColor(color);
    }

    @Override
    public void onCornerRadiusChanged(int radius) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Backgroundable)
            ((Backgroundable) item).setCornerRadius(radius);
        if (provider.getSelectedView() != null)
            provider.getSelectedView().setCornerRadius(radius);
    }

    @Override
    public void onNameColorChange(int color) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setNameColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onNameSizeChange(int size) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setNameFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onNameFontChange(Font font) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setNameFont(font);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onDescColorChange(int color) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setDescriptionColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onDescSizeChange(int size) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setDescriptionFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onDescFontChange(Font font) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setDescriptionFont(font);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceVisibleChange(boolean visible) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceVisible(visible);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceColorChange(int color) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceSizeChange(int size) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onPriceFontChange(Font font) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drinktextable)
            ((Drinktextable) item).setPriceFont(font);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onColumnCountChange(int columns) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof DrinkGroup)
            ((DrinkGroup) item).setColumnCount(columns);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onColumnSpacingChange(int space) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof DrinkGroup)
            ((DrinkGroup) item).setColumnSpacing(space);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onRowSpacingChange(int space) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof DrinkGroup)
            ((DrinkGroup) item).setRowSpacing(space);
        provider.getSelectedView().notifyChanged();
    }
}
