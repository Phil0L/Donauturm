package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.TextBottomSheet;
import com.pl.donauturm.drinksmenu.model.Font;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;
import com.pl.donauturm.drinksmenu.model.interfaces.Textable;

public class TextEventHandler extends ItemEventHandler implements TextBottomSheet.TextEvent {

    public TextEventHandler(EditorProvider provider) {
        super(provider);
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
    public void onColorChange(int color) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Textable)
            ((Textable) item).setColor(color);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onSizeChange(int size) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Textable)
            ((Textable) item).setFontSize(size);
        provider.getSelectedView().notifyChanged();
    }

    @Override
    public void onFontChange(Font font) {
        onAnyChange();
        DrinksMenuItem item = provider.getCurrentItem();
        if (item instanceof Textable)
            ((Textable) item).setFont(font);
        provider.getSelectedView().notifyChanged();
    }
}
