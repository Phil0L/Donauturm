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

    private List<DrinksMenu> items;
    private final HashMap<Integer, DrinksMenuFragment> fragmentCache;
    private final FragmentManager fragmentManager;
    private boolean showALoadingFragment;
    private long loadingId = 1000;
    private int loadingPos = 0;

    @SuppressWarnings("unused")
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

    public void addItem(DrinksMenu item){
        this.items.add(item);
        if (showALoadingFragment) {
            loadingPos++;
            loadingId = randomLong();
            notifyFragmentChanged(items.size() - 1);
            notifyItemInserted(items.size());
        } else
            notifyItemInserted(items.size() - 1);
    }

    public void removeItem(DrinksMenu item){
        this.items.remove(item);
        if (showALoadingFragment){
            loadingPos--;
            loadingId = randomLong();
            notifyFragmentChanged(items.size());
            notifyItemRemoved(items.size() + 1);
        } else
            notifyItemRemoved(items.size());
    }

    @SuppressWarnings("unused")
    public void updateItem(DrinksMenu item){
        int index = items.indexOf(item);
        if (index != -1) {
            items.set(index, item);
            notifyFragmentDataChanged(index, item);
        }
    }

    public void updateItemAt(int index, DrinksMenu item){
        items.set(index, item);
        notifyFragmentDataChanged(index, item);
    }

    public List<DrinksMenu> getItems() {
        return items;
    }

    public void showALoadingFragment(boolean showALoadingFragment) {
        this.showALoadingFragment = showALoadingFragment;
        if (showALoadingFragment) {
            loadingPos = items.size();
            notifyItemInserted(items.size());
        } else {
            notifyItemRemoved(items.size());
        }

    }

    public DrinksMenu getItem(int position){
        if (position < 0 || position >= items.size())
            return null;
        return items.get(position);
    }

    public Fragment getFragmentAt(int position){
        Fragment fragment = fragmentManager.findFragmentByTag("f" + getItemId(position));
        if (fragment != null) return fragment;
        return fragmentCache.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (showALoadingFragment && position >= items.size()) {
            return new DrinksMenuFragment.LoadingFragment();
        }
        if (position < 0 || position >= items.size())
            return null;
        DrinksMenuFragment drinksMenuFragment = DrinksMenuFragment.newInstance(items.get(position));
        fragmentCache.put(position, drinksMenuFragment);
        return drinksMenuFragment;
    }

    @Override
    public long getItemId(int position) {
        Fragment fragment = createFragment(position);
        if (fragment instanceof DrinksMenuFragment)
            return position;
        return position == loadingPos ? loadingId : super.getItemId(position);
    }

    @Override
    public boolean containsItem(long itemId) {
        if (loadingId == itemId)
            return true;
        return super.containsItem(itemId);
    }

    private long randomLong() {
        if (loadingId == 0)
            return loadingId = 1000;
        else return ++loadingId;
    }

    @Override
    public int getItemCount() {
        return items.size() + (showALoadingFragment ? 1 : 0);
    }

    private void notifyFragmentChanged(int position){
        notifyItemRemoved(position);
        notifyItemInserted(position);
        notifyItemChanged(position);
    }

    private void notifyFragmentDataChanged(int position, DrinksMenu drinksMenu){
        Fragment fragment = getFragmentAt(position);
        if (fragment instanceof DrinksMenuFragment)
            ((DrinksMenuFragment) fragment).onMenuLoaded(drinksMenu);
        notifyItemChanged(position);
    }

}
