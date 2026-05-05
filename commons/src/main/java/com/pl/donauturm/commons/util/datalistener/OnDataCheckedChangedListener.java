package com.pl.donauturm.commons.util.datalistener;

import android.widget.CompoundButton;

public interface OnDataCheckedChangedListener<D> extends OnDataListener<D, OnDataCheckedChangedListener<D>>, CompoundButton.OnCheckedChangeListener {

    @Override
    default void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        onData(getData(), isChecked);
    }

    @Override
    default void onData(D data){
        onData(data, true);
    }

    void onData(D data, boolean isChecked);

}

