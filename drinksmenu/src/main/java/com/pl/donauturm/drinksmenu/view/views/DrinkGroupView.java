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
import com.pl.donauturm.drinksmenu.model.content.Drink;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;
import com.pl.donauturm.drinksmenu.model.Item;

public class DrinkGroupView extends ItemView {

    private GridAdapter internalAdapter;

    public DrinkGroupView(Context context) {
        super(context);
    }

    public DrinkGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrinkGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GridAdapter newGridAdapter() {
        internalAdapter = new GridAdapter(getContext(), (DrinkGroup) item);
        return internalAdapter;
    }

    @Override
    public void notifyChanged() {
        setNumColumns(((DrinkGroup) item).getColumnCount());
        setHorizontalSpacing(((DrinkGroup) item).getColumnSpacing());
        setVerticalSpacing(((DrinkGroup) item).getRowSpacing());
        internalAdapter.notifyDataSetChanged();
    }

    public static class GridAdapter extends ArrayAdapter<Item> {
        private final Context context;
        private final DrinkGroup drinkGroup;


        public GridAdapter(Context context, DrinkGroup drinkGroup) {
            super(context, R.layout.pref_preview_drink);
            this.drinkGroup = drinkGroup;
            this.context = context;
        }

        @Override
        public int getCount() {
            return drinkGroup.getItems().size();
        }

        @Override
        public Item getItem(int position) {
            return drinkGroup.getItems().get(position);
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

            Drink drink = drinkGroup.getItems().get(position);
            TextView drinkName = convertView.findViewById(R.id.drink_name);
            TextView drinkDesc = convertView.findViewById(R.id.drink_description);
            TextView drinkPrice = convertView.findViewById(R.id.drink_price);
            drinkName.setText(drink.getName());
            drinkName.setTextColor(drinkGroup.getNameColor());
            drinkName.setTextSize(drinkGroup.getNameFontSize());
            if (drinkGroup.getNameFont() != null)
                drinkName.setTypeface(drinkGroup.getNameFont().getTypeFace());
            drinkDesc.setText(drink.getDescription());
            drinkDesc.setTextColor(drinkGroup.getDescriptionColor());
            drinkDesc.setTextSize(drinkGroup.getDescriptionFontSize());
            if (drinkGroup.getDescriptionFont() != null)
                drinkDesc.setTypeface(drinkGroup.getDescriptionFont().getTypeFace());
            drinkPrice.setVisibility(drinkGroup.isPriceVisible() ? VISIBLE : GONE);
            drinkPrice.setText(drink.getPriceFormatted());
            drinkPrice.setTextColor(drinkGroup.getPriceColor());
            drinkPrice.setTextSize(drinkGroup.getPriceFontSize());
            if (drinkGroup.getPriceFont() != null)
                drinkPrice.setTypeface(drinkGroup.getPriceFont().getTypeFace());
            return convertView;
        }
    }
}
