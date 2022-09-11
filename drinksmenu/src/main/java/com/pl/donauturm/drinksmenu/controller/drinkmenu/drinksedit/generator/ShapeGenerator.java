package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.MainFragmentDrinkMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.model.content.ShapeItem;
import com.pl.donauturm.drinksmenu.view.views.ShapeView;

public class ShapeGenerator {

    public ShapeView generateNewPreviewShape(FrameLayout parent, ShapeItem shapeItem){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shapeItem.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shapeItem.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shapeItem.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shapeItem.getTop());
        ShapeView shapeView = new ShapeView(parent.getContext());
        parent.addView(shapeView, layoutParams);
        return shapeView;
    }

    public ShapeView generateNewImageShape(FrameLayout parent, ShapeItem shapeItem, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) shapeItem.getWidth(), bg),
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) shapeItem.getHeight(), bg));
        layoutParams.leftMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) shapeItem.getLeft(), bg);
        layoutParams.topMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) shapeItem.getTop(), bg);
        ShapeView shapeView = new ShapeView(parent.getContext());
        shapeView.deactivateResize();
        shapeView.deactivateDrag();
        shapeView.deactivateClick();
        parent.addView(shapeView, layoutParams);
        return shapeView;
    }

}
