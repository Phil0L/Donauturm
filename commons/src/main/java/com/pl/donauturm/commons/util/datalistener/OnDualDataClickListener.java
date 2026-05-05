package com.pl.donauturm.commons.util.datalistener;

import android.view.View;

public interface OnDualDataClickListener<D, E> extends OnDualDataListener<D, E, OnDualDataClickListener<D,E>>, View.OnClickListener {

    @Override
    default void onClick(View v){
        onData();
    }
}
