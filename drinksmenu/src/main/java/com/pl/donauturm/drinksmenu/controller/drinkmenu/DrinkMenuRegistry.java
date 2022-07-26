package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.util.MapObservable;

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

    @Nullable
    public DrinksMenu put(String s, DrinksMenu drinksMenu) {
        if (containsKey(s)) {
            DrinksMenu old = get(s);
            if (old == null) return null;
            if (old.hasGreaterVersionThan(drinksMenu.getVersion())) {
                return old;
            } else {
                return replace(s, drinksMenu);
            }
        } else {
            DrinkMenus.put(s, drinksMenu);
            notifyAddition(s, drinksMenu);
            return drinksMenu;
        }
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
