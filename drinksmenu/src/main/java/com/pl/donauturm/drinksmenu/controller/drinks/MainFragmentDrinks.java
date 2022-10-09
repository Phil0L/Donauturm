package com.pl.donauturm.drinksmenu.controller.drinks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
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
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinkMenuRegistry;
import com.pl.donauturm.drinksmenu.controller.util.AreYouSure;
import com.pl.donauturm.drinksmenu.controller.util.CloudState;
import com.pl.donauturm.drinksmenu.controller.util.ListDifference;
import com.pl.donauturm.drinksmenu.controller.util.api.DMCredentials;
import com.pl.donauturm.drinksmenu.controller.util.api.DrinksMenuAPI;
import com.pl.donauturm.drinksmenu.databinding.FragmentDrinksBinding;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.util.MapObservable;
import com.pl.donauturm.drinksmenu.util.popupbubble.BubblePopup;
import com.pl.donauturm.drinksmenu.view.dialogs.AddEditDrinkDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.AutomaticMenuUpdateDialog;
import com.pl.donauturm.drinksmenu.view.dialogs.SetEditDMCDialog;
import com.pl.donauturm.drinksmenu.view.popup.LoadingCloudInfo;
import com.pl.donauturm.drinksmenu.view.popup.NoConnectionInfo;
import com.pl.donauturm.drinksmenu.view.popup.UpToDateInfo;
import com.pl.donauturm.pisignageapi.requests.Request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MainFragmentDrinks extends Fragment implements DrinksAdapter.OnActionListener, MapObservable.MapObserver<String, Drink> {

    private FragmentDrinksBinding binding;
    private DrinksAdapter adapter;
    private CloudState cloudState;
    private boolean inSync = false;
    private DrinksMenuAPI api;
    private final Set<String> editedDrinks = new ArraySet<>();
    private DMCredentials credentials;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrinksBinding.inflate(inflater, container, false);
        return binding.getRoot();
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

        binding.refresh.setOnRefreshListener(this::pullAgain);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerDrinks.setLayoutManager(layoutManager);
        adapter = new DrinksAdapter(binding.recyclerDrinks, new ArrayList<>(DrinkRegistry.getInstance().values()));
        adapter.addOnActionListener(this);
        binding.recyclerDrinks.setAdapter(adapter);
        if (binding.recyclerDrinks.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) binding.recyclerDrinks.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        DrinkRegistry.getInstance().observe(this);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(this::pull);
    }

    private void pull() {
        binding.refresh.setRefreshing(true);
        cloudState = CloudState.PULLING;
        requireActivity().invalidateOptionsMenu();

        Request.enableLazyAsyncLogin(credentials.getUsername(), credentials.getPassword());
        api = DrinksMenuAPI.simple(credentials.getUsername(), credentials.getPassword(), getContext());
//        binding.swipeRefresh.setEnabled(false);
        api.asynchronous.getAllDrinks(list -> {
//            binding.swipeRefresh.setEnabled(true);
            if (list == null) return;
            List<String> listBefore = new ArrayList<>(DrinkRegistry.getInstance().keySet());
            List<String> listAfter = list.stream().map(Drink::getId).distinct().collect(Collectors.toList());
            ListDifference<String> difference = new ListDifference<>(listBefore, listAfter);
            for (String id : difference.getList1Standalone()) {
                DrinkRegistry.getInstance().remove(id);
            }
            for (Drink drink : list) {
                DrinkRegistry.getInstance().put(drink);
            }

            onFinishedPulling();
        });
    }

    private void pullAgain() {
        pull();
    }

    private void onFinishedPulling() {
        binding.refresh.setRefreshing(false);
        cloudState = CloudState.UP_TO_DATE;
        inSync = true;
        requireActivity().invalidateOptionsMenu();
    }

    private void onErrorPulling() {
        cloudState = CloudState.NO_CONNECTION;
        requireActivity().invalidateOptionsMenu();
        new NoConnectionInfo(getActivity(), v -> pullAgain()).show(requireActivity().findViewById(R.id.cloud));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.drinks_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem cloudItem = menu.findItem(R.id.cloud);
        if (cloudItem != null && cloudState != null)
            cloudItem.setIcon(cloudState.getIconResource());
        MenuItem addDrinkItem = menu.findItem(R.id.add_drink);
        addDrinkItem.setVisible(inSync);
        boolean hasShowingPopup = BubblePopup.hasShowingPopup();
        BubblePopup.dismissAll();
        if (hasShowingPopup) {
            cloudClicked(requireActivity().findViewById(R.id.cloud));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Add drink":
                onAdd();
                return true;
            case "Cloud":
                cloudClicked(requireActivity().findViewById(R.id.cloud));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cloudClicked(View view) {
        switch (cloudState) {
            case UNKNOWN:
                break;
            case PULLING:
                new LoadingCloudInfo(getActivity(), "Currently downloading the drinks.", v -> {
                    cloudState = CloudState.READY_FOR_PULL;
                    requireActivity().invalidateOptionsMenu();
                }).show(view);
                break;
            case PUSHING:
                new LoadingCloudInfo(getActivity(), "Currently uploading the drinks.", v -> {
                    cloudState = CloudState.READY_FOR_PUSH;
                    requireActivity().invalidateOptionsMenu();
                }).show(view);
                break;
            case READY_FOR_PULL:
                pullAgain();
                break;
            case UP_TO_DATE:
                new UpToDateInfo(getActivity(), "Drinks are in sync with the server.", v -> uploadDrinks(api)).show(view);
                break;
            case READY_FOR_PUSH:
                uploadDrinks(api);
                break;
            case NO_CONNECTION:
                new NoConnectionInfo(getActivity(), v -> pullAgain()).show(view);
                break;
        }
    }

    private void somethingChangedFromUser(Drink changedDrink) {
        cloudState = CloudState.READY_FOR_PUSH;
        requireActivity().invalidateOptionsMenu();
        editedDrinks.add(changedDrink.getId());
    }

    private void uploadDrinks(DrinksMenuAPI api) {
        cloudState = CloudState.PUSHING;
        requireActivity().invalidateOptionsMenu();
        api.asynchronous.saveAllDrinks(new ArrayList<>(DrinkRegistry.getInstance().values()), v -> {
            cloudState = CloudState.UP_TO_DATE;
            requireActivity().invalidateOptionsMenu();
            prepareMenuUpdate();
        });
    }

    private void prepareMenuUpdate() {
        if (editedDrinks.isEmpty()) return;
        if (DrinkMenuRegistry.getInstance().isEmpty()) {
            api.asynchronous.getAllDrinkMenusIteratedWithoutImages(dm -> DrinkMenuRegistry.getInstance().put(dm), list -> prepareMenuUpdate());
            return;
        }
        Set<String> editedMenus = new ArraySet<>();
        for (String id : editedDrinks) {
            DrinkMenuRegistry.getInstance().values().stream()
                    .filter(menu -> menu.getAllDrinks().stream().anyMatch(drink -> drink.getDrinkId().equals(id)))
                    .forEach(menu -> editedMenus.add(menu.getId()));
        }
        if (!editedMenus.isEmpty()) {
            AutomaticMenuUpdateDialog.newInstance(getActivity(), new HashSet<>(editedDrinks), editedMenus, api).show(getChildFragmentManager(), "AutomaticMenuUpdateDialog");
        }
        editedDrinks.clear();

    }

    public void onAdd() {
        AddEditDrinkDialog addEditDrinkDialog = AddEditDrinkDialog.newInstance();
        addEditDrinkDialog.setOnSavedListener((n, d, p) -> {
            Drink drink = new Drink(n, d, p);
            DrinkRegistry.getInstance().put(drink);
            somethingChangedFromUser(drink);
        });
        addEditDrinkDialog.show(getChildFragmentManager(), "AddEditDrinkDialog");
    }

    @Override
    public void onDelete(Drink drink, int position) {
        AreYouSure areYouSure = AreYouSure.newInstance();
        areYouSure.setOnYesListener(() -> {
            DrinkRegistry.getInstance().remove(drink.getId());
            somethingChangedFromUser(drink);
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
            somethingChangedFromUser(drink);
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
        somethingChangedFromUser(drink);
    }

    @Override
    public void onStock(Drink drink, int position) {
        Drink actual = DrinkRegistry.getInstance().get(drink.getId());
        if (actual != null) {
            actual.setCrossedOut(!drink.isCrossedOut());
            DrinkRegistry.getInstance().replace(actual);
        }
        somethingChangedFromUser(drink);
    }

    @Override
    public void onAddition(int index, String source, Drink element, Map<String, Drink> map) {
        adapter.addItem(element);
    }

    @Override
    public void onRemoval(int index, String deletedSource, Drink deletedElement, Map<String, Drink> map) {
        int position = adapter.getPositionFromId(deletedElement.getId());
        adapter.removeItem(position);
    }

    @Override
    public void onUpdate(int index, String source, Drink oldElement, Drink newElement, Map<String, Drink> map) {
        int position = adapter.getPositionFromId(oldElement.getId());
        adapter.updateItem(newElement, position);
    }
}
