package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.MainFragmentDrinkMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.model.content.TextItem;
import com.pl.donauturm.drinksmenu.view.views.TextView;

public class TextGenerator {

    public TextView generateNewPreviewText(FrameLayout parent, TextItem textItem){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) textItem.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) textItem.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) textItem.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) textItem.getTop());
        TextView textView = new TextView(parent.getContext());
        parent.addView(textView, layoutParams);
        return textView;
    }

    public TextView generateNewImageText(FrameLayout parent, TextItem textItem, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) textItem.getWidth(), bg),
                MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) textItem.getHeight(), bg));
        layoutParams.leftMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) textItem.getLeft(), bg);
        layoutParams.topMargin = MainFragmentDrinkMenu.ValueScale.scalePositionToView((int) textItem.getTop(), bg);
        TextView textView = new TextView(parent.getContext());
        textView.deactivateResize();
        textView.deactivateDrag();
        textView.deactivateClick();
        parent.addView(textView, layoutParams);
        return textView;
    }

}
