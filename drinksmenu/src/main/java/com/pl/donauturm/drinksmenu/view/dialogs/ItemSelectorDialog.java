package com.pl.donauturm.drinksmenu.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.content.ShapeItem;
import com.pl.donauturm.drinksmenu.model.content.TextItem;

import java.util.List;

public class ItemSelectorDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    private OnItemSelectedListener itemSelectedListener;
    private ListView itemListView;

    private ItemSelectorDialog() {
    }

    public static ItemSelectorDialog newInstance() {
        return new ItemSelectorDialog();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            setOnItemSelectedListener((OnItemSelectedListener) context);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_select_drink, null);


        itemListView = rootView.findViewById(R.id.item_list_view);
        itemListView.setAdapter(new ItemsAdapter());
        itemListView.setOnItemClickListener(this);


        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Choose Item Type")
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<? extends DrinksMenuItem> name = ((ItemsAdapter) itemListView.getAdapter()).getItem(position);
        if (itemSelectedListener != null)
            itemSelectedListener.onItemSelected(name);
        dismiss();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Class<? extends DrinksMenuItem> itemClass);
    }

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public class ItemsAdapter extends BaseAdapter {

        private final List<Class<? extends DrinksMenuItem>> items;

        public ItemsAdapter() {
            this.items = List.of(
                    DrinkItem.class,
                    DrinkGroupItem.class,
                    TextItem.class,
                    ShapeItem.class
            );
        }


        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Class<? extends DrinksMenuItem> getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            // We use the position as ID
            return position;
        }

        private String getDescription(String name){
            switch (name) {
                case "Drink":
                    return "A simple View containing a single drink";
                case "DrinkGroup":
                    return "A view containing multiple drinks in a list or grid";
                case "Shape":
                    return "A colored box with rounded corners";
                case "Text":
                    return "Just some text";
            }
            return "";
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            // This function may be called in two cases: a new view needs to be created,
            // or an existing view needs to be reused
            if (view == null) {
                LayoutInflater li = LayoutInflater.from(getContext());
                view = li.inflate(R.layout.pref_click_dialog_item, parent, false);
            }
            view.setOnClickListener(v -> ItemSelectorDialog.this.onItemClick(ItemSelectorDialog.this.itemListView, v, position, getItemId(position)));
            TextView name = view.findViewById(R.id.item_name);
            name.setText(getItem(position).getSimpleName());
            TextView desc = view.findViewById(R.id.item_description);
            desc.setText(getDescription(getItem(position).getSimpleName()));
            return view;
        }
    }

}
