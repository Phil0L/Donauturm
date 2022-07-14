package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;

import java.util.ArrayList;
import java.util.List;

public class DrinksMenuAdapter extends FragmentStateAdapter {

    private List<DrinksMenu> items;

    public DrinksMenuAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.items = new ArrayList<>();
    }

    public DrinksMenuAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.items = new ArrayList<>();
    }

    public void setItems(List<DrinksMenu> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public List<DrinksMenu> getItems() {
        return items;
    }

    public DrinksMenu getItem(int position){
        if (position < 0 || position >= items.size())
            return null;
        return items.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DrinksMenuFragment.newInstance(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
