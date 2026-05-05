package com.pl.donauturm.commons.util.datalistener;

import android.view.View;

public interface OnDataLongClickListener<D> extends OnDataListener<D, OnDataLongClickListener<D>>, View.OnLongClickListener {

    @Override
    default boolean onLongClick(View v){
        return onDataB();
    }

    boolean onDataB(D data);

    default boolean onDataB(){
        return onDataB(getData());
    }

    @Override
    default void onData(D data){
        onDataB();
    }
}

