package com.pl.donauturm.commons.util.datalistener;

import android.view.View;

public interface OnDataClickListener<D> extends OnDataListener<D, OnDataClickListener<D>>, View.OnClickListener {

    @Override
    default void onClick(View v){
        onData();
    }
}

