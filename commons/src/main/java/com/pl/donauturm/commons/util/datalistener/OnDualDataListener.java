package com.pl.donauturm.commons.util.datalistener;

import android.util.Pair;

public interface OnDualDataListener<D, E, CLASS extends OnDualDataListener<D, E, CLASS>> extends OnDataListener<Pair<D, E>, CLASS> {

    default CLASS withData(D data1, E data2) {
        return OnDataListener.super.withData(new Pair<>(data1, data2));
    }

    @Override
    default void onData(Pair<D, E> data) {
        onData(data.first, data.second);
    }

    void onData(D data1, E data2);

    default D getFirstData() {
        return getData().first;
    }

    default E getSecondData() {
        return getData().second;
    }
}
