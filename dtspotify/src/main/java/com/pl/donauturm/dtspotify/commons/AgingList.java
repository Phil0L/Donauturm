package com.pl.donauturm.dtspotify.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AgingList<T> extends ArrayList<T> {

    private final Map<T, Integer> timeToLive = new HashMap<>();

    public void add(T item, int ttl){
        timeToLive.put(item, ttl);
        add(item);
    }

    public void age(){
        for (Map.Entry<T, Integer> kv : new ArrayList<>(timeToLive.entrySet())) {
            int ttl = kv.getValue() - 1;
            T entry = kv.getKey();
            if (ttl == 0)
                timeToLive.remove(entry);
            timeToLive.replace(entry, ttl);
        }
    }
}
