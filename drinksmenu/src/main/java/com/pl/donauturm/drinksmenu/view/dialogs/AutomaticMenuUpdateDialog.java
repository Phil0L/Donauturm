package com.pl.donauturm.drinksmenu.view.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuRegistry;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuRenderer;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.databinding.DialogAutomaticMenuUpdateBinding;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AutomaticMenuUpdateDialog extends BottomSheetDialogFragment {

    private DialogAutomaticMenuUpdateBinding binding;

    private Activity context;
    private Set<String> editedDrinks;
    private Set<DrinksMenu> editedMenus;
    private DrinksMenuAPI api;


    public AutomaticMenuUpdateDialog() {
        // Required empty public constructor
    }

    public static AutomaticMenuUpdateDialog newInstance(Activity context, Set<String> editedDrinks, Set<String> editedMenus, DrinksMenuAPI api) {
        AutomaticMenuUpdateDialog fragment = new AutomaticMenuUpdateDialog();
        fragment.context = context;
        fragment.editedDrinks = editedDrinks;
        fragment.editedMenus = editedMenus.stream().map(id -> DrinksMenuRegistry.getInstance().get(id)).collect(Collectors.toSet());
        fragment.api = api;
        return fragment;
    }

    public static AutomaticMenuUpdateDialog newInstance() {
        return new AutomaticMenuUpdateDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAutomaticMenuUpdateBinding.inflate(inflater, container, false);

        String text = String.format(Locale.getDefault(), "While this is uploading... \nYou have edited %d Drinks. \nThis affected %d Drink menus. \n Do you want to update these?", editedDrinks.size(), editedMenus.size());
        binding.infoText.setText(text);
        showUpdate();

        return binding.getRoot();
    }

    private void showUpdate() {
        binding.generateImages.setVisibility(View.VISIBLE);
        binding.uploadMenus.setVisibility(View.GONE);
        binding.deploy.setVisibility(View.GONE);
        binding.generateImages.setActivated(true);
        binding.generateImages.setEnabled(true);
        binding.generateImages.setOnClickListener((v) -> {
            binding.generateImages.setActivated(false);
            binding.generateImages.setEnabled(false);
            AtomicInteger count = new AtomicInteger();
            for (DrinksMenu menu : editedMenus) {
                if (menu.getBackGround() == null) {
                    if (menu instanceof DrinksMenuCloud)
                        ((DrinksMenuCloud) menu).provideApi(api);
                    menu.requireBackGround(context, dm ->
                            new DrinksMenuRenderer().renderFromMenu(context, menu, bitmap -> {
                                int completed = count.incrementAndGet();
                                if (completed == editedMenus.size()) {
                                    showUpload();
                                }
                            }));
                    continue;
                }
                new DrinksMenuRenderer().renderFromMenu(context, menu, bitmap -> {
                    int completed = count.incrementAndGet();
                    if (completed == editedMenus.size()) {
                        showUpload();
                    }
                });
            }
        });
    }

    private void showUpload() {
        binding.generateImages.setVisibility(View.GONE);
        binding.uploadMenus.setVisibility(View.VISIBLE);
        binding.deploy.setVisibility(View.GONE);
        binding.uploadMenus.setActivated(true);
        binding.uploadMenus.setEnabled(true);
        binding.uploadMenus.setOnClickListener((v) -> {
            binding.uploadMenus.setActivated(false);
            binding.uploadMenus.setEnabled(false);
            AtomicInteger count = new AtomicInteger();
            for (DrinksMenu menu : editedMenus) {
                api.asynchronous.uploadDrinkMenu(menu, vo -> {
                    int completed = count.incrementAndGet();
                    if (completed == editedMenus.size()) {
                        showDeploy();
                    }
                });
            }
        });
    }

    private void showDeploy() {
        binding.generateImages.setVisibility(View.GONE);
        binding.uploadMenus.setVisibility(View.GONE);
        binding.deploy.setVisibility(View.VISIBLE);
        binding.deploy.setActivated(true);
        binding.deploy.setEnabled(true);
        binding.deploy.setOnClickListener((v) -> {
            binding.deploy.setActivated(false);
            binding.deploy.setEnabled(false);
//            api.asynchronous.deployAll(() -> {
            dismiss();
//            });
        });
    }

}
