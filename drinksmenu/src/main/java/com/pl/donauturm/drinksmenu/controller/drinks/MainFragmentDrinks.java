package com.pl.donauturm.drinksmenu.controller.drinks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.util.AreYouSure;
import com.pl.donauturm.drinksmenu.databinding.FragmentDrinksBinding;
import com.pl.donauturm.drinksmenu.model.Drink;

import java.util.ArrayList;

public class MainFragmentDrinks extends Fragment implements DrinksAdapter.OnActionListener {

    private DrinksAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        FragmentDrinksBinding binding = FragmentDrinksBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerDrinks.setLayoutManager(layoutManager);

        adapter = new DrinksAdapter(binding.recyclerDrinks, new ArrayList<>(DrinkRegistry.getInstance().values()));
        adapter.addOnActionListener(this);
        binding.recyclerDrinks.setAdapter(adapter);
        if (binding.recyclerDrinks.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) binding.recyclerDrinks.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.drinks_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Add drink":
                onAdd();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAdd(){
        AddEditDrinkDialog addEditDrinkDialog = AddEditDrinkDialog.newInstance();
        addEditDrinkDialog.setOnSavedListener((n, d, p) -> {
            Drink drink = new Drink(n, d, p);
            DrinkRegistry.getInstance().put(drink);
            adapter.addItem(drink);
        });
        addEditDrinkDialog.show(getChildFragmentManager(), "AddEditDrinkDialog");
    }

    @Override
    public void onDelete(Drink drink, int position) {
        AreYouSure areYouSure = AreYouSure.newInstance();
        areYouSure.setOnYesListener(() -> {
            DrinkRegistry.getInstance().remove(drink.getId());
            adapter.removeItem(position);
        });
        areYouSure.show(getChildFragmentManager(), "AreYouSure");
    }

    @Override
    public void onEdit(Drink drink, int position) {
        AddEditDrinkDialog addEditDrinkDialog = AddEditDrinkDialog.newInstance(drink.getName(), drink.getDescription(), drink.getPrice());
        addEditDrinkDialog.setOnSavedListener((n, d, p) -> {
            Drink actual = DrinkRegistry.getInstance().get(drink.getId());
            if (actual != null) {
                actual.setName(n);
                actual.setDescription(d);
                actual.setPrice(p);
                DrinkRegistry.getInstance().replace(actual);
            }
            adapter.updateItem(drink, position);
        });
        addEditDrinkDialog.show(getChildFragmentManager(), "AddEditDrinkDialog");
    }

    @Override
    public void onHide(Drink drink, int position) {
        Drink actual = DrinkRegistry.getInstance().get(drink.getId());
        if (actual != null) {
            actual.setHidden(!drink.isHidden());
            DrinkRegistry.getInstance().replace(actual);
        }
        adapter.updateItem(drink, position);
    }

    @Override
    public void onStock(Drink drink, int position) {
        Drink actual = DrinkRegistry.getInstance().get(drink.getId());
        if (actual != null) {
            actual.setCrossedOut(!drink.isCrossedOut());
            DrinkRegistry.getInstance().replace(actual);
        }
        adapter.updateItem(drink, position);
    }
}
