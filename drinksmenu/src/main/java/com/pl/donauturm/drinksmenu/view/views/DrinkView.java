package com.pl.donauturm.drinksmenu.view.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.Item;

public class DrinkView extends ItemView {

    private SingleAdapter internalAdapter;

    public DrinkView(Context context) {
        super(context);
    }

    public DrinkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrinkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SingleAdapter newSingleAdapter() {
        internalAdapter = new SingleAdapter(getContext(), (Drink) item);
        return internalAdapter;
    }

    @Override
    public void notifyChanged() {
        internalAdapter.notifyDataSetChanged();
    }

    public static class SingleAdapter extends ArrayAdapter<Item> {
        private final Context context;
        private final Drink drink;


        public SingleAdapter(Context context, Drink drink) {
            super(context, R.layout.pref_preview_drink);
            this.drink = drink;
            this.context = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Item getItem(int position) {
            return drink;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.pref_preview_drink, parent, false);

            TextView drinkName = convertView.findViewById(R.id.drink_name);
            TextView drinkDesc = convertView.findViewById(R.id.drink_description);
            TextView drinkPrice = convertView.findViewById(R.id.drink_price);
            drinkName.setText(drink.getName());
            drinkName.setTextColor(drink.getNameColor());
            drinkName.setTextSize(drink.getNameFontSize());
            if (drink.getNameFont() != null)
                drinkName.setTypeface(drink.getNameFont().getTypeFace());
            drinkDesc.setText(drink.getDescription());
            drinkDesc.setTextColor(drink.getDescriptionColor());
            drinkDesc.setTextSize(drink.getDescriptionFontSize());
            if (drink.getDescriptionFont() != null)
                drinkDesc.setTypeface(drink.getDescriptionFont().getTypeFace());
            drinkPrice.setVisibility(drink.isPriceVisible() ? VISIBLE : GONE);
            drinkPrice.setText(drink.getPriceFormatted());
            drinkPrice.setTextColor(drink.getPriceColor());
            drinkPrice.setTextSize(drink.getPriceFontSize());
            if (drink.getPriceFont() != null)
                drinkPrice.setTypeface(drink.getPriceFont().getTypeFace());
            return convertView;
        }
    }
}
