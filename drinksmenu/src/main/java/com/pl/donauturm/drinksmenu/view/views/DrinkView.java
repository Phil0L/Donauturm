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
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;

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
        internalAdapter = new SingleAdapter(getContext(), (DrinkItem) item);
        return internalAdapter;
    }

    @Override
    public void notifyChanged() {
        internalAdapter.notifyDataSetChanged();
    }

    public static class SingleAdapter extends ArrayAdapter<DrinksMenuItem> {
        private final Context context;
        private final DrinkItem drinkItem;


        public SingleAdapter(Context context, DrinkItem drinkItem) {
            super(context, R.layout.pref_preview_drink);
            this.drinkItem = drinkItem;
            this.context = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public DrinksMenuItem getItem(int position) {
            return drinkItem;
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
            drinkName.setText(drinkItem.getName());
            drinkName.setTextColor(drinkItem.getNameColor());
            drinkName.setTextSize(drinkItem.getNameFontSize());
            if (drinkItem.getNameFont() != null)
                drinkName.setTypeface(drinkItem.getNameFont().getTypeFace());
            drinkDesc.setText(drinkItem.getDescription());
            drinkDesc.setTextColor(drinkItem.getDescriptionColor());
            drinkDesc.setTextSize(drinkItem.getDescriptionFontSize());
            if (drinkItem.getDescriptionFont() != null)
                drinkDesc.setTypeface(drinkItem.getDescriptionFont().getTypeFace());
            drinkPrice.setVisibility(drinkItem.isPriceVisible() ? VISIBLE : GONE);
            drinkPrice.setText(drinkItem.getPriceFormatted());
            drinkPrice.setTextColor(drinkItem.getPriceColor());
            drinkPrice.setTextSize(drinkItem.getPriceFontSize());
            if (drinkItem.getPriceFont() != null)
                drinkPrice.setTypeface(drinkItem.getPriceFont().getTypeFace());
            return convertView;
        }
    }
}
