package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrinksMenuAdapter extends FragmentStateAdapter {

    private HashMap<Integer, DrinksMenuFragment> fragmentCache;
    private List<DrinksMenu> items;
    private FragmentManager fragmentManager;

    public DrinksMenuAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.items = new ArrayList<>();
        this.fragmentManager = fragmentActivity.getSupportFragmentManager();
        this.fragmentCache = new HashMap<>();
    }

    public DrinksMenuAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.items = new ArrayList<>();
        this.fragmentManager = fragmentManager;
        this.fragmentCache = new HashMap<>();
    }

    @SuppressLint("NotifyDataSetChanged")
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

    public Fragment getFragmentAt(int position){
        DrinksMenuFragment fragment = fragmentCache.get(position);
        if (fragment != null) return fragment;
        return fragmentManager.findFragmentByTag("f" + getItemId(position));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DrinksMenuFragment drinksMenuFragment = DrinksMenuFragment.newInstance(items.get(position));
        fragmentCache.put(position, drinksMenuFragment);
        return drinksMenuFragment;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
