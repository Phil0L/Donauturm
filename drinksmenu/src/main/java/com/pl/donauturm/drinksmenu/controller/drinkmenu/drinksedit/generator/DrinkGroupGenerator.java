package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.MainitemDrinksMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.view.views.DrinkGroupView;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;

public class DrinkGroupGenerator {

    public DrinkGroupView generateNewPreviewDrinkGroup(FrameLayout parent, DrinkGroup drinkGroup){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroup.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroup.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroup.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroup.getTop());
        DrinkGroupView drinkGroupView = new DrinkGroupView(parent.getContext());
        parent.addView(drinkGroupView, layoutParams);
        return drinkGroupView;
    }

    public DrinkGroupView generateNewImageDrinkGroup(FrameLayout parent, DrinkGroup drinkGroup, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainitemDrinksMenu.ValueScale.scalePositionToView((int) drinkGroup.getWidth(), bg),
                MainitemDrinksMenu.ValueScale.scalePositionToView((int) drinkGroup.getHeight(), bg));
        layoutParams.leftMargin = MainitemDrinksMenu.ValueScale.scalePositionToView((int) drinkGroup.getLeft(), bg);
        layoutParams.topMargin = MainitemDrinksMenu.ValueScale.scalePositionToView((int) drinkGroup.getTop(), bg);
        DrinkGroupView drinkGroupView = new DrinkGroupView(parent.getContext());
        drinkGroupView.deactivateResize();
        drinkGroupView.deactivateDrag();
        drinkGroupView.deactivateClick();
        parent.addView(drinkGroupView, layoutParams);
        return drinkGroupView;
    }
}
