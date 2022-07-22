package com.pl.donauturm.drinksmenu.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.content.Drink;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroup;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinkRegistry;
import com.pl.donauturm.drinksmenu.model.content.DrinkStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddDrinkGroupDialog extends DialogFragment implements SearchView.OnQueryTextListener, DialogInterface.OnClickListener {

    private OnDrinkGroupSelectedListener drinkGroupSelectedListener;
    private ListView drinkListView;

    private AddDrinkGroupDialog() {
    }

    public static AddDrinkGroupDialog newInstance() {
        return new AddDrinkGroupDialog();
    }

    public void setOnDrinkGroupSelectedListener(OnDrinkGroupSelectedListener drinkSelectedListener) {
        this.drinkGroupSelectedListener = drinkSelectedListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDrinkGroupSelectedListener) {
            setOnDrinkGroupSelectedListener((OnDrinkGroupSelectedListener) context);
        }
    }

    private List<Drink> getValue(){
        if (drinkListView != null && drinkListView.getAdapter() instanceof DrinksAdapter)
            return ((DrinksAdapter) drinkListView.getAdapter()).selected;
        return new ArrayList<>();
    }

    private List<Drink> getDrinks(){
        return new ArrayList<>(DrinkRegistry.DRINKS);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_select_drinks, null);

        List<Drink> drink = getValue();
        List<Drink> drinks = getDrinks();
        drinkListView = rootView.findViewById(R.id.drink_list_view);
        drinkListView.setAdapter(new DrinksAdapter(drinks, drink));
        SearchView drinkSearch = rootView.findViewById(R.id.drink_search);
        drinkSearch.setQuery("", false);
        drinkSearch.setOnQueryTextListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle("Choose Drinks")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (drinkGroupSelectedListener != null)
            drinkGroupSelectedListener.onDrinkGroupSelected(new DrinkGroup("New Drink Group", getValue(), new DrinkStyle()));
        dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        DrinksAdapter drinksAdapter = ((DrinksAdapter) drinkListView.getAdapter());
        drinksAdapter.filter(query);
        drinksAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        DrinksAdapter drinksAdapter = ((DrinksAdapter) drinkListView.getAdapter());
        drinksAdapter.filter(newText);
        drinksAdapter.notifyDataSetChanged();
        return false;
    }

    public interface OnDrinkGroupSelectedListener {
        void onDrinkGroupSelected(DrinkGroup drinkGroup);
    }

    // Font adaptor responsible for redrawing the item TextView with the appropriate font.
    // We use BaseAdapter since we need both arrays, and the effort is quite small.
    public class DrinksAdapter extends BaseAdapter {

        private final List<Drink> drinks;
        private List<Drink> filteredDrinks;
        private final List<Drink> selected;

        public DrinksAdapter(List<Drink> drinks, List<Drink> selected) {
            this.drinks = new ArrayList<>(drinks);
            this.filteredDrinks = new ArrayList<>(drinks);
            this.selected = selected;
        }

        public void filter(String text){
            filteredDrinks = drinks.stream().filter(d
                    -> d.getName().toLowerCase().contains(text.toLowerCase())
                    || d.getDescription().toLowerCase().contains(text.toLowerCase())
            ).collect(Collectors.toList());
        }

        public void clicked(Drink drink, boolean checked){
            if (checked && !selected.contains(drink))
                selected.add(drink);
            else if(!checked)
                selected.remove(drink);
        }

        @Override
        public int getCount() {
            return filteredDrinks.size();
        }

        @Override
        public Drink getItem(int position) {
            return filteredDrinks.get(position);
        }

        @Override
        public long getItemId(int position) {
            // We use the position as ID
            return drinks.indexOf(filteredDrinks.get(position));
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
                tv.setChecked(selected.contains(filteredDrinks.get(position)));
                tv.setOnClickListener(v -> {
                    tv.toggle();
                    clicked(getItem(position), tv.isChecked());
                });
                TextView name = view.findViewById(R.id.drink_name);
                name.setText(filteredDrinks.get(position).getName());
                TextView desc = view.findViewById(R.id.drink_description);
                desc.setText(filteredDrinks.get(position).getDescription());

            }
            return view;
        }
    }

}
