package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.bottomsheet.ShapeBottomSheet;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.interfaces.Backgroundable;

public class ShapeEventHandler extends ItemEventHandler implements ShapeBottomSheet.ShapeEvent {

    public ShapeEventHandler(EditorProvider provider) {
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
}
