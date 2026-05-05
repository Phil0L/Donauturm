package com.pl.donauturm.commons.util.lists;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class LimitedList<E> extends ArrayList<E> {

    protected int limit = 1;

    public LimitedList() {
    }

    public LimitedList<E> fromElements(E... elements){
        LimitedList<E> limitedList = new LimitedList<>();
        limitedList.setLimit(elements.length);
        limitedList.addAll(Arrays.stream(elements).collect(Collectors.toList()));
        return limitedList;
    }

    public LimitedList(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E e) {
        if (e == null) return false;
        super.add(e);
        while (size() > getLimit()){
            super.remove(0);
        }
        return true;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        if (c.isEmpty()) return false;
        for (E e : c) {
            add(e);
        }
        return true;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(@NonNull UnaryOperator<E> operator) {
        throw new UnsupportedOperationException();
    }


}
