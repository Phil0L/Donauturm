package com.pl.donauturm.drinksmenu.view.preferences.drink;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.controller.drinks.DrinkRegistry;

import java.util.ArrayList;
import java.util.List;

public class DrinkPreference extends Preference implements DrinkDialog.OnDrinkSelectedListener {

    private List<DrinkItem> drinkItems;
    private DrinkItem currentDrinkItem;

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public DrinkPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();
        DrinkDialog fragment = DrinkDialog.newInstance(this);
        fragment.setOnDrinkSelectedListener(this);
        fragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "drink_" + getKey());
    }

    public DrinkItem getValue() {
        return currentDrinkItem;
    }

    public void setValue(DrinkItem drinkItem) {
        if (drinkItem != null)
            currentDrinkItem = drinkItem;
    }

    public List<DrinkItem> getDrinks() {
        if (drinkItems == null) loadFonts();
        return drinkItems;
    }

    private void loadFonts() {
        drinkItems = new ArrayList<>(DrinkRegistry.getInstance().values());
    }

    @Override
    public void onDrinkSelected(DrinkItem drinkItem) {
        currentDrinkItem = drinkItem;
        notifyChanged();
        callChangeListener(drinkItem);
    }


}
