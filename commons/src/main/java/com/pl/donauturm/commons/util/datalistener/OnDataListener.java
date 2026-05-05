package com.pl.donauturm.commons.util.datalistener;

import android.util.ArrayMap;

import java.util.Map;

public interface OnDataListener<D, CLASS extends OnDataListener<D,CLASS>> {

    Map<OnDataListener<?,?>, Object> storage = new ArrayMap<>();

    @SuppressWarnings("unchecked")
    default CLASS withData(D data){
        storage.put(this, data);
        return (CLASS) this;
    }

    void onData(D data);

    default void onData(){
        onData(getData());
    }

    @SuppressWarnings("unchecked")
    default D getData(){
        try {
            return (D) storage.getOrDefault(this, null);
        } catch (ClassCastException cce){
            return null;
        }
    }
}

