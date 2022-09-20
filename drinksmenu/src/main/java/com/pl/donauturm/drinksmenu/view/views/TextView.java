package com.pl.donauturm.drinksmenu.view.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.content.TextItem;

public class TextView extends ItemView {

    private SingleAdapter internalAdapter;

    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SingleAdapter newSingleAdapter() {
        internalAdapter = new SingleAdapter(getContext(), (TextItem) item);
        return internalAdapter;
    }

    @Override
    public void notifyChanged() {
        internalAdapter.notifyDataSetChanged();
    }

    public static class SingleAdapter extends ArrayAdapter<DrinksMenuItem> {
        private final Context context;
        private final TextItem textItem;


        public SingleAdapter(Context context, TextItem textItem) {
            super(context, R.layout.pref_text);
            this.textItem = textItem;
            this.context = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public DrinksMenuItem getItem(int position) {
            return textItem;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.pref_text, parent, false);

            android.widget.TextView tv = convertView.findViewById(R.id.text);
            tv.setText(textItem.getName());
            tv.setTextColor(textItem.getColor());
            tv.setTextSize(textItem.getFontSize());
            if (textItem.getFont() != null)
                tv.setTypeface(textItem.getFont().getTypeFace());
            return convertView;
        }
    }
}
