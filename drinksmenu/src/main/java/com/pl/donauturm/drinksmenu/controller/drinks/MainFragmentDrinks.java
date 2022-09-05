package com.pl.donauturm.drinksmenu.controller.drinks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pl.donauturm.drinksmenu.databinding.FragmentDrinksBinding;

import java.util.ArrayList;

public class MainFragmentDrinks extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDrinksBinding binding = FragmentDrinksBinding.inflate(inflater, container, false);

        binding.recyclerDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerDrinks.setAdapter(new DrinksAdapter(binding.recyclerDrinks, new ArrayList<>(DrinkRegistry.getInstance().values())));

        return binding.getRoot();
    }
}
