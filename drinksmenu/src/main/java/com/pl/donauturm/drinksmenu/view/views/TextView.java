package com.pl.donauturm.drinksmenu.view.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.Item;
import com.pl.donauturm.drinksmenu.model.content.Text;

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
        internalAdapter = new SingleAdapter(getContext(), (Text) item);
        return internalAdapter;
    }

    @Override
    public void notifyChanged() {
        internalAdapter.notifyDataSetChanged();
    }

    public static class SingleAdapter extends ArrayAdapter<Item> {
        private final Context context;
        private final Text text;


        public SingleAdapter(Context context, Text text) {
            super(context, R.layout.pref_preview_text);
            this.text = text;
            this.context = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Item getItem(int position) {
            return text;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.pref_preview_text, parent, false);

            android.widget.TextView tv = convertView.findViewById(R.id.text);
            tv.setText(text.getName());
            tv.setTextColor(text.getColor());
            tv.setTextSize(text.getFontSize());
            if (text.getFont() != null)
                tv.setTypeface(text.getFont().getTypeFace());
            return convertView;
        }
    }
}
