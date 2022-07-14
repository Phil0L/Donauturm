package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.DrinkBottomSheet;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Drinktextable;

public class DrinkEventHandler extends ItemEventHandler implements DrinkBottomSheet.DrinkEvent {

    public DrinkEventHandler(EditorProvider provider) {
        super(provider);
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
    public void onDrinkChanged(Drink newDrink) {
        onAnyChange();
        Item item = provider.getCurrentItem();
        if (item instanceof Drink){
            item.setName(newDrink.getName());
            ((Drink) item).setDescription(newDrink.getDescription());
            ((Drink) item).setPrice(newDrink.getPrice());
        }
        provider.getSelectedView().notifyChanged();
    }

}
