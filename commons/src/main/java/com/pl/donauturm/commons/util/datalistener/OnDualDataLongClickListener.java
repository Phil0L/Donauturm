package com.pl.donauturm.commons.util.datalistener;

import android.util.Pair;
import android.view.View;

public interface OnDualDataLongClickListener<D, E> extends OnDualDataListener<D, E, OnDualDataLongClickListener<D, E>>, View.OnLongClickListener {

    @Override
    default boolean onLongClick(View v){
        return onDataB();
    }

    boolean onDataB(D data1, E data2);

    default boolean onDataB(Pair<D,E> data){
        return onDataB(data.first, data.second);
    }

    default boolean onDataB(){
        return onDataB(getData());
    }

    @Override
    default void onData(D data1, E data2){
        onDataB();
    }
}

