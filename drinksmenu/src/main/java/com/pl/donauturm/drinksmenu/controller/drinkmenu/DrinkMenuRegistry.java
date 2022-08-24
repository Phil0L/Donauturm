package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.model.DrinksMenuLocal;
import com.pl.donauturm.drinksmenu.util.MapObservable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DrinkMenuRegistry extends MapObservable<String, DrinksMenu> implements Map<String, DrinksMenu> {

    private static final DrinkMenuRegistry instance = new DrinkMenuRegistry();

    private DrinkMenuRegistry() {
        provideMap(this);
    }

    public static DrinkMenuRegistry getInstance() {
        if (instance != null) return instance;
        else return new DrinkMenuRegistry();
    }

    public Map<String, DrinksMenu> DrinkMenus = new LinkedHashMap<>();

    public int size() {
        return DrinkMenus.size();
    }

    public boolean isEmpty() {
        return DrinkMenus.isEmpty();
    }

    public boolean containsKey(@Nullable Object o) {
        return DrinkMenus.containsKey(o);
    }

    public boolean containsValue(@Nullable Object o) {
        return DrinkMenus.containsValue(o);
    }

    @Nullable
    public DrinksMenu get(@Nullable Object o) {
        return DrinkMenus.get(o);
    }

    public String getKey(DrinksMenu drinksMenu) {
        return DrinkMenus.entrySet()
                .stream().filter(e -> e.getValue().getName().equals(drinksMenu.getName()))
                .findFirst().orElse(new AbstractMap.SimpleEntry<>(null, null)).getKey();
    }

    @Nullable
    public DrinksMenu put(String s, DrinksMenu drinksMenu) {
        if (containsKey(s)) {
            DrinksMenu old = get(s);
            return checkBeforeReplace(old, drinksMenu);
        } else {
            drinksMenu = checkBeforePut(drinksMenu);
            DrinkMenus.put(s, drinksMenu);
            notifyAddition(s, drinksMenu);
            return drinksMenu;
        }
    }

    private DrinksMenu checkBeforePut(DrinksMenu drinksMenu) {
        if (drinksMenu instanceof DrinksMenuLocal) {
            drinksMenu.setCloudState(DrinksMenu.CloudState.PULLING);
        }
        if (drinksMenu instanceof DrinksMenuCloud) {
            drinksMenu.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
        }
        return drinksMenu;
    }

    private DrinksMenu checkBeforeReplace(DrinksMenu older, DrinksMenu newer) {
        if (older == null) {
            remove(newer.getName());
            return put(newer.getName(), newer);
        }

        // if the older is a local and the newer is a cloud
        if (older instanceof DrinksMenuLocal && newer instanceof DrinksMenuCloud) {
            if (older.getVersion().equals(((DrinksMenuLocal) older).getCloudVersion())) {
                //local was uploaded, replace local with cloud if cloud is newer, set up to date
                if (newer.hasGreaterVersionThan(older.getVersion())) {
                    //cloud is newer, replace local with cloud
                    replace(newer.getName(), newer);
                    newer.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
                    return newer;
                }
                older.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
                return older;
            } else if (newer.hasGreaterVersionThan(((DrinksMenuLocal) older).getCloudVersion())) {
                //local and cloud have some changes, TODO give user comparison
                return null;

            } else if (older.hasGreaterVersionThan(newer.getVersion())) {
                //local is newer, set ready to push
                older.setCloudState(DrinksMenu.CloudState.READY_FOR_PUSH);
                return older;
            } else {
                //local is up to date with cloud
                older.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
                return older;

            }
        }

        // if the older is a local and the newer is a local
        if (older instanceof DrinksMenuLocal && newer instanceof DrinksMenuLocal) {
            if (newer.hasGreaterVersionThan(older.getVersion())) {
                replace(newer.getName(), newer);
                newer.setCloudState(older.getCloudState());
                return newer;
            }
            if (((DrinksMenuLocal) newer).hasGreaterCloudVersionThan(((DrinksMenuLocal) older).getCloudVersion())) {
                replace(newer.getName(), newer);
                newer.setCloudState(older.getCloudState());
                return newer;
            }
        }

        // if the newer is more recent than the older
        if (newer.hasGreaterVersionThan(older.getVersion())) {
            replace(newer.getName(), newer);
            newer.setCloudState(DrinksMenu.CloudState.UP_TO_DATE);
            return newer;
        }

        return older;
    }

    @Nullable
    public DrinksMenu remove(@Nullable Object o) {
        if (o instanceof String) {
            DrinksMenu dm = DrinkMenus.get(o);
            if (dm == null) return null;
            int index = Arrays.asList(DrinkMenus.keySet().toArray()).indexOf(dm);
            DrinksMenu drinksMenu = DrinkMenus.remove(o);
            notifyRemoval(index, (String) o, drinksMenu);
            return drinksMenu;
        } else {
            return null;
        }
    }

    public void putAll(@NonNull Map<? extends String, ? extends DrinksMenu> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }


    @NonNull
    public Set<String> keySet() {
        return DrinkMenus.keySet();
    }

    @NonNull
    public Collection<DrinksMenu> values() {
        return DrinkMenus.values();
    }

    @NonNull
    public Set<Map.Entry<String, DrinksMenu>> entrySet() {
        return DrinkMenus.entrySet();
    }

    public DrinksMenu replace(String s, DrinksMenu drinkMenu) {
        DrinksMenu dm = DrinkMenus.get(s);
        if (dm == null) return null;
        DrinkMenus.replace(s, drinkMenu);
        int index = Arrays.asList(DrinkMenus.keySet().toArray()).indexOf(s);
        notifyUpdate(index, dm, s, drinkMenu);
        return drinkMenu;
    }


}
