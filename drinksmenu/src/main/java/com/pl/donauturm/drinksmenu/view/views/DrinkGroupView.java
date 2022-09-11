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
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;

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
        internalAdapter = new GridAdapter(getContext(), (DrinkGroupItem) item);
        return internalAdapter;
    }

    @Override
    public void notifyChanged() {
        setNumColumns(((DrinkGroupItem) item).getColumnCount());
        setHorizontalSpacing(((DrinkGroupItem) item).getColumnSpacing());
        setVerticalSpacing(((DrinkGroupItem) item).getRowSpacing());
        internalAdapter.notifyDataSetChanged();
    }

    public static class GridAdapter extends ArrayAdapter<DrinksMenuItem> {
        private final Context context;
        private final DrinkGroupItem drinkGroupItem;


        public GridAdapter(Context context, DrinkGroupItem drinkGroupItem) {
            super(context, R.layout.pref_preview_drink);
            this.drinkGroupItem = drinkGroupItem;
            this.context = context;
        }

        @Override
        public int getCount() {
            return drinkGroupItem.getItems().size();
        }

        @Override
        public DrinksMenuItem getItem(int position) {
            return drinkGroupItem.getItems().get(position);
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

            DrinkItem drinkItem = drinkGroupItem.getItems().get(position);
            TextView drinkName = convertView.findViewById(R.id.drink_name);
            TextView drinkDesc = convertView.findViewById(R.id.drink_description);
            TextView drinkPrice = convertView.findViewById(R.id.drink_price);
            drinkName.setText(drinkItem.getName());
            drinkName.setTextColor(drinkGroupItem.getNameColor());
            drinkName.setTextSize(drinkGroupItem.getNameFontSize());
            if (drinkGroupItem.getNameFont() != null)
                drinkName.setTypeface(drinkGroupItem.getNameFont().getTypeFace());
            drinkDesc.setText(drinkItem.getDescription());
            drinkDesc.setTextColor(drinkGroupItem.getDescriptionColor());
            drinkDesc.setTextSize(drinkGroupItem.getDescriptionFontSize());
            if (drinkGroupItem.getDescriptionFont() != null)
                drinkDesc.setTypeface(drinkGroupItem.getDescriptionFont().getTypeFace());
            drinkPrice.setVisibility(drinkGroupItem.isPriceVisible() ? VISIBLE : GONE);
            drinkPrice.setText(drinkItem.getPriceFormatted());
            drinkPrice.setTextColor(drinkGroupItem.getPriceColor());
            drinkPrice.setTextSize(drinkGroupItem.getPriceFontSize());
            if (drinkGroupItem.getPriceFont() != null)
                drinkPrice.setTypeface(drinkGroupItem.getPriceFont().getTypeFace());
            return convertView;
        }
    }
}
