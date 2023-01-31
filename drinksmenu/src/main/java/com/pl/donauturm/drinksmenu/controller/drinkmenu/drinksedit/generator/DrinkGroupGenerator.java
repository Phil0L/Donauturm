package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.MainFragmentDrinksMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.view.views.DrinkGroupView;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;

public class DrinkGroupGenerator {

    public DrinkGroupView generateNewPreviewDrinkGroup(FrameLayout parent, DrinkGroupItem drinkGroupItem){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroupItem.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroupItem.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroupItem.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkGroupItem.getTop());
        DrinkGroupView drinkGroupView = new DrinkGroupView(parent.getContext());
        parent.addView(drinkGroupView, layoutParams);
        return drinkGroupView;
    }

    public DrinkGroupView generateNewImageDrinkGroup(FrameLayout parent, DrinkGroupItem drinkGroupItem, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainFragmentDrinksMenu.ValueScale.scalePositionToView((int) drinkGroupItem.getWidth(), bg),
                MainFragmentDrinksMenu.ValueScale.scalePositionToView((int) drinkGroupItem.getHeight(), bg));
        layoutParams.leftMargin = MainFragmentDrinksMenu.ValueScale.scalePositionToView((int) drinkGroupItem.getLeft(), bg);
        layoutParams.topMargin = MainFragmentDrinksMenu.ValueScale.scalePositionToView((int) drinkGroupItem.getTop(), bg);
        DrinkGroupView drinkGroupView = new DrinkGroupView(parent.getContext());
        drinkGroupView.deactivateResize();
        drinkGroupView.deactivateDrag();
        drinkGroupView.deactivateClick();
        parent.addView(drinkGroupView, layoutParams);
        return drinkGroupView;
    }
}
