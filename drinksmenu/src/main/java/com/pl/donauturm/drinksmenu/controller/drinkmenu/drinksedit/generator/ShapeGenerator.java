package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.MainitemDrinksMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.view.views.ShapeView;
import com.pl.donauturm.drinksmenu.model.content.Shape;

public class ShapeGenerator {

    public ShapeView generateNewPreviewShape(FrameLayout parent, Shape shape){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shape.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shape.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shape.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) shape.getTop());
        ShapeView shapeView = new ShapeView(parent.getContext());
        parent.addView(shapeView, layoutParams);
        return shapeView;
    }

    public ShapeView generateNewImageShape(FrameLayout parent, Shape shape, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainitemDrinksMenu.ValueScale.scalePositionToView((int) shape.getWidth(), bg),
                MainitemDrinksMenu.ValueScale.scalePositionToView((int) shape.getHeight(), bg));
        layoutParams.leftMargin = MainitemDrinksMenu.ValueScale.scalePositionToView((int) shape.getLeft(), bg);
        layoutParams.topMargin = MainitemDrinksMenu.ValueScale.scalePositionToView((int) shape.getTop(), bg);
        ShapeView shapeView = new ShapeView(parent.getContext());
        shapeView.deactivateResize();
        shapeView.deactivateDrag();
        shapeView.deactivateClick();
        parent.addView(shapeView, layoutParams);
        return shapeView;
    }

}
