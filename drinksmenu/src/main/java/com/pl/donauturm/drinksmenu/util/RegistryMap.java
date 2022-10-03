package com.pl.donauturm.drinksmenu.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.drinksmenu.model.interfaces.Id;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class RegistryMap<V extends Id> extends MapObservable<String, V>  implements Map<String, V> {

    public Map<String, V> Data = new LinkedHashMap<>();

    public RegistryMap() {
        List<V> defaults = defaultValues();
        if (defaults != null && !defaults.isEmpty()) {
            for (V value : defaults) {
                put(value.getId(), value);
            }
        }
        provideMap(this);

    }

    public int size() {
        return Data.size();
    }

    public boolean isEmpty() {
        return Data.isEmpty();
    }

    public boolean containsValue(@Nullable Object o) {
        return Data.containsValue(o);
    }

    @Override
    public boolean containsKey(@Nullable Object o) {
        return Data.containsKey(o);
    }

    @Nullable
    public V get(@Nullable Object o) {
        return Data.get(o);
    }

    public String getKey(V v) {
        return Data.entrySet()
                .stream().filter(e -> e.getValue().getId().equals(v.getId()))
                .findFirst().orElse(new AbstractMap.SimpleEntry<>(null, null)).getKey();
    }

    @Nullable
    public V remove(@Nullable Object o) {
        if (o instanceof String) {
            V dm = Data.get(o);
            if (dm == null) return null;
            int index = Arrays.asList(Data.keySet().toArray()).indexOf(dm);
            V drinksMenu = Data.remove(o);
            notifyRemoval(index, (String) o, drinksMenu);
            return drinksMenu;
        } else {
            return null;
        }
    }

    @NonNull
    public Set<String> keySet() {
        return Data.keySet();
    }

    @NonNull
    public Collection<V> values() {
        return Data.values();
    }

    @NonNull
    public Set<Map.Entry<String, V>> entrySet() {
        return Data.entrySet();
    }

    public V put(V value){
        if (containsValue(value)) {
            return replace(value);
        } else {
            Data.put(value.getId(), value);
            notifyAddition(value.getId(), value);
            return value;
        }
    }

    @SafeVarargs
    public final void put(V... values){
        for (V value : values) {
            put(value);
        }
    }

    @Nullable
    @Override
    public V put(String s, V v) {
        return put(v);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends V> map) {
        map.forEach(this::put);
    }

    public V replace(V v){
        V dm = Data.get(v.getId());
        if (dm == null) return null;
        Data.replace(v.getId(), v);
        int index = Arrays.asList(Data.keySet().toArray()).indexOf(v.getId());
        notifyUpdate(index, dm, v.getId(), v);
        return v;
    }

    public V replace(String s, V v) {
        return replace(v);
    }

    public List<V> defaultValues() {
        return null;
    }


}
