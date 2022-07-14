package com.pl.donauturm.drinksmenu.controller;

import static com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinkMenuRegistry.DrinkMenus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuAdapter;
import com.pl.donauturm.drinksmenu.databinding.FragmentDrinksMenuBinding;
import com.pl.donauturm.drinksmenu.model.DrinkRegistry;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;

import java.util.ArrayList;

public class MainitemDrinksMenu extends Fragment {

    private FragmentDrinksMenuBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        {
            DrinkMenus.put("Getränkekarte",
                    new DrinksMenu("Getränkekarte", requireContext(),
                            DrinkRegistry.DRINKS.iterator().next()));
            DrinkMenus.put("Bier",
                    new DrinksMenu("Bier", requireContext()));
        } // debug

        binding = FragmentDrinksMenuBinding.inflate(inflater, container, false);
        DrinksMenuAdapter drinksMenuAdapter = new DrinksMenuAdapter(requireActivity().getSupportFragmentManager(), getLifecycle());
        drinksMenuAdapter.setItems(new ArrayList<>(DrinkMenus.values()));
        binding.drinksMenuPager.setAdapter(drinksMenuAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.drinksMenuTabs, binding.drinksMenuPager, (tab, position) -> tab.setText(drinksMenuAdapter.getItem(position).getName()));
        tabLayoutMediator.attach();
        return binding.getRoot();
    }

    public static class ValueScale {

        private static int previewWidth = 0;
        private static int bitmapWidth = 0;

        public static int scaleViewToPosition(int draggedPosition, ImageView view) {
            if (bitmapWidth == 0)
                bitmapWidth = view.getDrawable().getIntrinsicWidth();
            if (previewWidth == 0) previewWidth = view.getWidth();
            float factor = (float) bitmapWidth / previewWidth;
            return (int) (draggedPosition * factor);
        }

        public static int scalePositionToView(int draggedPosition, ImageView view) {
            if (bitmapWidth == 0)
                bitmapWidth = view.getDrawable().getIntrinsicWidth();
            if (previewWidth == 0) previewWidth = view.getWidth();
            float factor = (float) bitmapWidth / previewWidth;
            return (int) (draggedPosition / factor);
        }
    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    public static void largeLog(String tag, Object content) {
        largeLog(tag, content.toString());
    }

    public static void largeLog(String tag, int content) {
        largeLog(tag, String.valueOf(content));
    }

}