package com.pl.donauturm.commons.util.datalistener;

import android.util.Pair;
import android.widget.CompoundButton;

public interface OnDualDataCheckedChangedListener<D, E> extends OnDualDataListener<D, E, OnDualDataCheckedChangedListener<D, E>>, CompoundButton.OnCheckedChangeListener {

    @Override
    default void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        onData(getData(), isChecked);
    }

    @Override
    default void onData(D data1, E data2){
        onData(data1, data2, true);
    }

    @Override
    default void onData(Pair<D, E> data) {
        onData(data.first, data.second, true);
    }

    default void onData(Pair<D, E> data, boolean isChecked) {
        onData(data.first, data.second, isChecked);
    }

    void onData(D data1, E data2, boolean isChecked);

}

