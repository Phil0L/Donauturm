package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.MainFragmentDrinkMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.view.views.DrinkView;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;

public class DrinkGenerator {

    public DrinkView generateNewPreviewDrink(FrameLayout parent, DrinkItem drinkItem){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkItem.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkItem.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkItem.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drinkItem.getTop());
        DrinkView drinkView = new DrinkView(parent.getContext());
        parent.addView(drinkView, layoutParams);
        return drinkView;
    }

    public DrinkView generateNewImageDrink(FrameLayout parent, DrinkItem drinkItem, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drinkItem.getWidth(), bg),
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drinkItem.getHeight(), bg));
        layoutParams.leftMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drinkItem.getLeft(), bg);
        layoutParams.topMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drinkItem.getTop(), bg);
        DrinkView drinkView = new DrinkView(parent.getContext());
        drinkView.deactivateDrag();
        drinkView.deactivateResize();
        drinkView.deactivateClick();
        parent.addView(drinkView, layoutParams);
        return drinkView;
    }
}
