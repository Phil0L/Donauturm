package com.pl.donauturm.drinksmenu.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinkMenuRegistry;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuAdapter;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuFragment;
import com.pl.donauturm.drinksmenu.databinding.FragmentDrinksMenuBinding;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.util.MapObservable;
import com.pl.donauturm.pisignageapi.apicontroller.AsyncPiSignageAPI;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

//TODO: some todos: editor:
    // hide bottomsheet when back pressed
    // hide bottomsheet when clicked on frame

public class MainitemDrinksMenu extends Fragment implements AsyncPiSignageAPI.APICallback<DrinksMenu>,
        MapObservable.MapObserver<String, DrinksMenu>, TabLayoutMediator.TabConfigurationStrategy {

    private FragmentDrinksMenuBinding binding;
    private DrinksMenuAdapter drinksMenuAdapter;
    private DrinksMenuAPI api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentDrinksMenuBinding.inflate(inflater, container, false);
        drinksMenuAdapter = new DrinksMenuAdapter(requireActivity().getSupportFragmentManager(), getLifecycle());
        drinksMenuAdapter.setItems(new ArrayList<>(DrinkMenuRegistry.getInstance().values())); //TODO: probably unnecessary
        binding.drinksMenuPager.setAdapter(drinksMenuAdapter);
        binding.drinksMenuPager.registerOnPageChangeCallback(new PageChangeListener());
        DrinkMenuRegistry.getInstance().observe(this);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.drinksMenuTabs, binding.drinksMenuPager, this);
        tabLayoutMediator.attach();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        api = DrinksMenuAPI.simple("philippletschka", "S4T2x9F@yEKYnA3", getContext());
        pull();
    }

    private void pull(){
        drinksMenuAdapter.showALoadingFragment(true);
        api.asynchronous.getAllDrinkMenusIteratedWithoutImages(this, list -> {
            drinksMenuAdapter.showALoadingFragment(false);
        });
    }

    private void pullAgain(){
        drinksMenuAdapter.showALoadingFragment(true);
        api.asynchronous.getAllDrinkMenusIterated(this, list -> {
            drinksMenuAdapter.showALoadingFragment(false);
        });
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        DrinksMenu drinksMenu = drinksMenuAdapter.getItem(position);
        if (drinksMenu != null) {
            tab.setText(drinksMenu.getName());
            tab.setIcon(null);
        }else {
            tab.setText("");
            tab.setIcon(R.drawable.anim_loading);
            if (tab.getIcon() instanceof AnimatedVectorDrawable)
                ((AnimatedVectorDrawable) tab.getIcon()).start();
        }
    }

    @Override
    public void onData(DrinksMenu data) {
        if (data == null) return;
        DrinksMenu drinksMenu = DrinkMenuRegistry.getInstance().put(data.getName(), data);
        if (drinksMenu == data && drinksMenu instanceof DrinksMenuCloud) {
            DrinksMenuCloud menu = ((DrinksMenuCloud) drinksMenu);
            // drinks menu is accepted
            if (drinksMenu.getBackGround() == null) {
                // image is not yet loaded
                menu.setLoading();
                Bitmap background = api.getBitmap(menu.getBackgroundUrl());
                menu.provideBackGround(background);
            }
        }
    }

    @Override
    public void onAddition(int index, String source, DrinksMenu element, Map<String, DrinksMenu> map) {
        element.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
        drinksMenuAdapter.addItem(element);
    }

    @Override
    public void onRemoval(int index, String deletedSource, DrinksMenu deletedElement, Map<String, DrinksMenu> map) {
        drinksMenuAdapter.removeItem(deletedElement);
    }

    @Override
    public void onUpdate(int index, String source, DrinksMenu oldElement, DrinksMenu newElement, Map<String, DrinksMenu> map) {
        drinksMenuAdapter.updateItemAt(index, newElement);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.drinks_menu_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem cloudItem = menu.findItem(R.id.cloud);
        DrinksMenuFragment currentFragment = getCurrentDrinksMenuFragment();
        if (currentFragment == null) {
            cloudItem.setVisible(false);
        } else {
            cloudItem.setVisible(true);
            cloudItem.setIcon(currentFragment.getDrinksMenu().getCloudState().getIconResource());
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Cloud":
                cloudClicked();
                return true;
            case "Pull all Drink menus":
                pullAgain();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cloudClicked(){
        int pageIndex = binding.drinksMenuPager.getCurrentItem();
        Fragment fragment = drinksMenuAdapter.getFragmentAt(pageIndex);
        if (fragment instanceof DrinksMenuFragment) {
            ((DrinksMenuFragment) fragment).cloudClicked(api);
        }
    }

    private DrinksMenuFragment getCurrentDrinksMenuFragment(){
        int pageIndex = binding.drinksMenuPager.getCurrentItem();
        Fragment fragment = drinksMenuAdapter.getFragmentAt(pageIndex);
        if (fragment instanceof DrinksMenuFragment) {
            return (DrinksMenuFragment) fragment;
        }
        return null;
    }

    private DrinksMenuFragment getDrinksMenuFragmentAt(int index){
        Fragment fragment = drinksMenuAdapter.getFragmentAt(index);
        if (fragment instanceof DrinksMenuFragment) {
            return (DrinksMenuFragment) fragment;
        }
        return null;
    }

    public static class ValueScale {

        private static int previewWidth = 0;
        private static int bitmapWidth = 0;

        @SuppressWarnings("unused")
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

    private class PageChangeListener extends OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            requireActivity().invalidateOptionsMenu();
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

    @SuppressWarnings("unused")
    public static void largeLog(String tag, int content) {
        largeLog(tag, String.valueOf(content));
    }

}