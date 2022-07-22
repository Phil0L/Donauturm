package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.generator;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pl.donauturm.drinksmenu.controller.MainitemDrinksMenu;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.view.views.TextView;
import com.pl.donauturm.drinksmenu.model.content.Text;

public class TextGenerator {

    public TextView generateNewPreviewText(FrameLayout parent, Text text){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) text.getWidth()),
                DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) text.getHeight()));
        layoutParams.leftMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) text.getLeft());
        layoutParams.topMargin = DrinksMenuEditorActivity.ValueScale.with(parent.getContext()).scalePositionToView((int) text.getTop());
        TextView textView = new TextView(parent.getContext());
        parent.addView(textView, layoutParams);
        return textView;
    }

    public TextView generateNewImageText(FrameLayout parent, Text text, ImageView bg){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                MainitemDrinksMenu.ValueScale.scalePositionToView((int) text.getWidth(), bg),
                MainitemDrinksMenu.ValueScale.scalePositionToView((int) text.getHeight(), bg));
        layoutParams.leftMargin = MainitemDrinksMenu.ValueScale.scalePositionToView((int) text.getLeft(), bg);
        layoutParams.topMargin = MainitemDrinksMenu.ValueScale.scalePositionToView((int) text.getTop(), bg);
        TextView textView = new TextView(parent.getContext());
        textView.deactivateResize();
        textView.deactivateDrag();
        textView.deactivateClick();
        parent.addView(textView, layoutParams);
        return textView;
    }

}
