package com.pl.donauturm.drinksmenu.view.preferences.drink;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.controller.drinks.DrinkRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DrinkDialog extends DialogFragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private OnDrinkSelectedListener drinkSelectedListener;
    private DrinkPreference preference;
    private ListView drinkListView;

    public DrinkDialog() {
    }

    public static DrinkDialog newInstance(DrinkPreference preference) {
        DrinkDialog dialog = new DrinkDialog();
        dialog.preference = preference;
        return dialog;
    }

    public void setOnDrinkSelectedListener(OnDrinkSelectedListener fontSelectedListener) {
        this.drinkSelectedListener = fontSelectedListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDrinkSelectedListener) {
            setOnDrinkSelectedListener((OnDrinkSelectedListener) context);
        }
    }

    private DrinkItem getValue(){
        if (preference != null)
            return preference.getValue();
        return null;
    }

    private List<DrinkItem> getDrinks(){
        if (preference != null)
            return preference.getDrinks();
        return new ArrayList<>(DrinkRegistry.getInstance().values());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_select_drinks, null);

        DrinkItem drinkItem = getValue();
        List<DrinkItem> drinkItems = getDrinks();
        drinkListView = rootView.findViewById(R.id.drink_list_view);
        drinkListView.setAdapter(new DrinkAdapter(drinkItems, drinkItem));
        drinkListView.setOnItemClickListener(this);
        SearchView drinkSearch = rootView.findViewById(R.id.drink_search);
        drinkSearch.setQuery("", false);
        drinkSearch.setOnQueryTextListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Choose Drink")
                .setNegativeButton("ABBRECHEN", null)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DrinkItem drinkItem = ((DrinkItem) drinkListView.getAdapter().getItem(position));
        if (drinkSelectedListener != null)
            drinkSelectedListener.onDrinkSelected(drinkItem);
        dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        DrinkAdapter drinkAdapter = ((DrinkAdapter) drinkListView.getAdapter());
        drinkAdapter.filter(query);
        drinkAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        DrinkAdapter drinkAdapter = ((DrinkAdapter) drinkListView.getAdapter());
        drinkAdapter.filter(newText);
        drinkAdapter.notifyDataSetChanged();
        return false;
    }

    public interface OnDrinkSelectedListener {
        void onDrinkSelected(DrinkItem drinkItem);
    }

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public class DrinkAdapter extends BaseAdapter {

        private final List<DrinkItem> drinkItems;
        private List<DrinkItem> filteredDrinkItems;
        private final DrinkItem selected;

        public DrinkAdapter(List<DrinkItem> drinkItems, DrinkItem selected) {
            this.drinkItems = new ArrayList<>(drinkItems);
            this.filteredDrinkItems = new ArrayList<>(drinkItems);
            this.selected = selected;
        }

        public void filter(String text){
            filteredDrinkItems = drinkItems.stream().filter(d
                    -> d.getName().toLowerCase().contains(text.toLowerCase())
                    || d.getDescription().toLowerCase().contains(text.toLowerCase())
            ).collect(Collectors.toList());
        }

        @Override
        public int getCount() {
            return filteredDrinkItems.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredDrinkItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            // We use the position as ID
            return drinkItems.indexOf(filteredDrinkItems.get(position));
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            // This function may be called in two cases: a new view needs to be created,
            // or an existing view needs to be reused
            if (view == null) {
                // Since we're using the system list for the layout, use the system inflater
                final LayoutInflater inflater = (LayoutInflater)
                        requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // And inflate the view android.R.layout.select_dialog_singlechoice
                // Why? See com.android.internal.app.AlertController method createListView()
                view = inflater.inflate(R.layout.pref_select_dialog_drinkchoice, parent, false);
            }

            if (view != null) {
                // Find the text view from our interface
                CheckedTextView tv = view.findViewById(R.id.checked_box);
                tv.setChecked(filteredDrinkItems.get(position).equals(selected));
                TextView name = view.findViewById(R.id.drink_name);
                name.setText(filteredDrinkItems.get(position).getName());
                TextView desc = view.findViewById(R.id.drink_description);
                desc.setText(filteredDrinkItems.get(position).getDescription());

            }

            return view;
        }
    }
}
