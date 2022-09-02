package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.MainFragmentDrinkMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.view.views.DrinkView;
import com.pl.donauturm.drinksmenu.model.content.Drink;

public class DrinkGenerator {

    public DrinkView generateNewPreviewDrink(FrameLayout parent, Drink drink){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drink.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drink.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drink.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) drink.getTop());
        DrinkView drinkView = new DrinkView(parent.getContext());
        parent.addView(drinkView, layoutParams);
        return drinkView;
    }

    public DrinkView generateNewImageDrink(FrameLayout parent, Drink drink, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drink.getWidth(), bg),
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drink.getHeight(), bg));
        layoutParams.leftMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drink.getLeft(), bg);
        layoutParams.topMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) drink.getTop(), bg);
        DrinkView drinkView = new DrinkView(parent.getContext());
        drinkView.deactivateDrag();
        drinkView.deactivateResize();
        drinkView.deactivateClick();
        parent.addView(drinkView, layoutParams);
        return drinkView;
    }
}
