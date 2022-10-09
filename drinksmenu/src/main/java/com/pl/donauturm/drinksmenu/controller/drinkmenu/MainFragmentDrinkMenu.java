package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit.DrinksMenuEditorActivity;
import com.pl.donauturm.drinksmenu.controller.util.AreYouSure;
import com.pl.donauturm.drinksmenu.controller.util.CloudState;
import com.pl.donauturm.drinksmenu.controller.util.CloudState.OnCloudStateChangedListener;
import com.pl.donauturm.drinksmenu.controller.util.ListDifference;
import com.pl.donauturm.drinksmenu.controller.util.api.DMCredentials;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.databinding.FragmentDrinksMenuBinding;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.util.MapObservable;
import com.pl.donauturm.drinksmenu.view.dialogs.NewDrinksmenuDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.SetEditDMCDialog;
import com.pl.donauturm.pisignageapi.apicontroller.AsyncPiSignageAPI;
import com.pl.donauturm.pisignageapi.requests.Request;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: some todos: editor:
// change background
// change name
// add image view

public class MainFragmentDrinkMenu extends Fragment implements AsyncPiSignageAPI.APICallback<DrinksMenu>,
        MapObservable.MapObserver<String, DrinksMenu>, TabLayoutMediator.TabConfigurationStrategy,
        SwipeRefreshLayout.OnRefreshListener, OnCloudStateChangedListener {

    private FragmentDrinksMenuBinding binding;
    private DrinksMenuAdapter drinksMenuAdapter;
    private DrinksMenuAPI api;
    private DMCredentials credentials;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrinksMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        DrinkMenuRegistry.getInstance().observers.remove(this);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        credentials = DMCredentials.getCredentials(requireContext());
        if (credentials == null) {
            SetEditDMCDialog.newInstance(requireContext()).setOnSavedListener((u, p) -> {
                DMCredentials creds = new DMCredentials(u, p);
                creds.save(requireContext());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> onViewCreated(view, savedInstanceState));
            }).show(getParentFragmentManager(), "SetEditDMCDialog");
            return;
        }
        setHasOptionsMenu(true);
        drinksMenuAdapter = new DrinksMenuAdapter(getChildFragmentManager(), getLifecycle());
        drinksMenuAdapter.setItems(new ArrayList<>(DrinkMenuRegistry.getInstance().values()));
        binding.drinksMenuPager.setAdapter(drinksMenuAdapter);
        binding.drinksMenuPager.registerOnPageChangeCallback(new PageChangeListener());
        binding.swipeRefresh.setOnRefreshListener(this);
        DrinkMenuRegistry.getInstance().observe(this);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.drinksMenuTabs, binding.drinksMenuPager, this);
        tabLayoutMediator.attach();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            load();
            pull();
        });

    }

    private void load() {
        LocalDrinksMenuManager.loadAllLocalSavesAsync(requireContext());
    }

    private void pull() {
        Request.enableLazyAsyncLogin(credentials.getUsername(), credentials.getPassword());
        if (api == null)
            api = DrinksMenuAPI.simple(credentials.getUsername(), credentials.getPassword(), getContext());
        DrinksMenuEditorActivity.provideApi(api);
        binding.swipeRefresh.setEnabled(false);
        List<String> listBefore = new ArrayList<>(DrinkMenuRegistry.getInstance().keySet());
        api.asynchronous.getAllDrinkMenusIteratedWithoutImages(this, list -> {
            List<String> listAfter = list.stream().map(DrinksMenu::getId).distinct().collect(Collectors.toList());
            ListDifference<String> difference = new ListDifference<>(listBefore, listAfter);
            for (String id : difference.getList1Standalone()) {
                DrinkMenuRegistry.getInstance().remove(id);
            }
            binding.swipeRefresh.setEnabled(true);
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void pullAgain() {
        binding.swipeRefresh.setRefreshing(true);
        DrinkMenuRegistry.getInstance().values().forEach(dm -> {
            if (dm.getCloudState().isAbleToOverwrite())
                dm.setCloudState(CloudState.PULLING);
        });
        pull();
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        DrinksMenu drinksMenu = drinksMenuAdapter.getItem(position);
        if (drinksMenu != null) {
            tab.setText(drinksMenu.getName());
            tab.setIcon(null);
        } else {
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
        if (drinksMenu instanceof DrinksMenuCloud) {
            DrinksMenuCloud menu = ((DrinksMenuCloud) drinksMenu);
            // drinks menu is accepted
            if (drinksMenu.getBackGround() == null) {
                // image is not yet loaded
                menu.setLoading();
                api.asynchronous.getBitmap(menu.getBackgroundUrl(), bitmap -> {
                    menu.provideBackGround(bitmap);
                    drinksMenu.setCloudState(CloudState.UP_TO_DATE);
                    requireActivity().invalidateOptionsMenu();
                });
            } else {
                drinksMenu.setCloudState(CloudState.UP_TO_DATE);
                requireActivity().invalidateOptionsMenu();
            }
        }
    }

    @Override
    public void onAddition(int index, String source, DrinksMenu element, Map<String, DrinksMenu> map) {
        element.onCloudStateChanged(this);
        drinksMenuAdapter.addItem(element);
        if (element.isLoading()) {
            element.onLoaded(el -> LocalDrinksMenuManager.saveLocalSaveAsync(requireContext(), element));
            return;
        }
        LocalDrinksMenuManager.saveLocalSaveAsync(requireContext(), element);
    }

    @Override
    public void onRemoval(int index, String deletedSource, DrinksMenu deletedElement, Map<String, DrinksMenu> map) {
        drinksMenuAdapter.removeItem(deletedElement);
        LocalDrinksMenuManager.deleteLocalSave(requireContext(), deletedElement.getName());
    }

    @Override
    public void onUpdate(int index, String source, DrinksMenu oldElement, DrinksMenu newElement, Map<String, DrinksMenu> map) {
        newElement.onCloudStateChanged(this);
        drinksMenuAdapter.updateItemAt(index, newElement);
        if (newElement.isLoading()) {
            newElement.onLoaded(el -> LocalDrinksMenuManager.saveLocalSaveAsync(requireContext(), newElement));
            return;
        }
        LocalDrinksMenuManager.saveLocalSaveAsync(requireContext(), newElement);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.drinks_menu_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem cloudItem = menu.findItem(R.id.cloud);
        MenuItem addItem = menu.findItem(R.id.add_menu);
        MenuItem cloneItem = menu.findItem(R.id.clone_menu);
        MenuItem deleteItem = menu.findItem(R.id.delete_menu);
        DrinksMenuFragment currentFragment = getCurrentDrinksMenuFragment();
        if (currentFragment == null) {
            cloudItem.setVisible(false);
            addItem.setVisible(true);
            cloneItem.setVisible(false);
            deleteItem.setVisible(false);
        } else {
            cloudItem.setVisible(true);
            addItem.setVisible(true);
            cloneItem.setVisible(true);
            deleteItem.setVisible(true);
            if (currentFragment.getDrinksMenu() != null && currentFragment.getDrinksMenu().getCloudState() != null) {
                cloudItem.setIcon(currentFragment.getDrinksMenu().getCloudState().getIconResource());
            }
        }
    }

    @Override
    public void onRefresh() {
        pullAgain();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Cloud":
                cloudClicked(requireActivity().findViewById(R.id.cloud));
                return true;
            case "Add drink menu":
                addClicked();
                return true;
            case "Clone drink menu":
                cloneClicked();
                return true;
            case "Delete drink menu":
                deleteClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteClicked() {
        AreYouSure areYouSure = new AreYouSure();
        areYouSure.setOnYesListener(() -> {
            DrinksMenuFragment currentFragment = getCurrentDrinksMenuFragment();
            if (currentFragment == null) return;
            DrinksMenu drinksMenu = currentFragment.getDrinksMenu();
            if (drinksMenu == null) return;
            DrinkMenuRegistry.getInstance().remove(drinksMenu.getId());
            api.asynchronous.deleteDrinkMenu(drinksMenu, null);
        });
        areYouSure.show(getChildFragmentManager(), "AreYouSure");
    }

    @Override
    public void onCloudStateChanged(CloudState state) {
        if (getActivity() != null)
            getActivity().invalidateOptionsMenu();
    }

    private void cloudClicked(View v) {
        DrinksMenuFragment currentFragment = getCurrentDrinksMenuFragment();
        if (currentFragment == null) return;
        currentFragment.cloudClicked(api, v);
    }

    private void addClicked() {
        NewDrinksmenuDialog dialog = NewDrinksmenuDialog.newInstance();
        dialog.setOnTextSelectedListener(name -> {
            DrinksMenu drinksMenu = new DrinksMenu(name, requireContext()).createNewId();
            drinksMenu.onCloudStateChanged(this);
            drinksMenu.setCloudState(CloudState.READY_FOR_PUSH);
            DrinkMenuRegistry.getInstance().put(name, drinksMenu);
        });
        dialog.show(getChildFragmentManager(), "NewDrinksmenuDialog");
    }

    private void cloneClicked() {
        DrinksMenuFragment currentFragment = getCurrentDrinksMenuFragment();
        if (currentFragment == null) return;
        DrinksMenu cDrinksMenu = currentFragment.getDrinksMenu();
        NewDrinksmenuDialog dialog = NewDrinksmenuDialog.newInstance();
        dialog.setOnTextSelectedListener(name -> {
            DrinksMenu drinksMenu = cDrinksMenu.clone(name);
            drinksMenu.onCloudStateChanged(this);
            drinksMenu.setCloudState(CloudState.READY_FOR_PUSH);
            DrinkMenuRegistry.getInstance().put(name, drinksMenu);
        });
        dialog.show(getChildFragmentManager(), "NewDrinksmenuDialog");
    }

    private DrinksMenuFragment getCurrentDrinksMenuFragment() {
        int pageIndex = binding.drinksMenuPager.getCurrentItem();
        Fragment fragment = drinksMenuAdapter.getFragmentAt(pageIndex);
        if (fragment instanceof DrinksMenuFragment) {
            return (DrinksMenuFragment) fragment;
        }
        return null;
    }

    private DrinksMenuFragment getDrinksMenuFragmentAt(int index) {
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

        @Override
        public void onPageScrollStateChanged(int state) {
            if (!binding.swipeRefresh.isRefreshing())
                binding.swipeRefresh.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
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