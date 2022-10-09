package com.pl.donauturm.drinksmenu.controller.util;

import java.util.ArrayList;
import java.util.List;

public class ListDifference<T> {

    private List<T> list1;
    private List<T> list2;

    public ListDifference(List<T> list1, List<T> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public ListDifference(){

    }

    public List<T> getList1() {
        return list1;
    }

    public ListDifference<T> setList1(List<T> list1) {
        this.list1 = list1;
        return this;
    }

    public List<T> getList2() {
        return list2;
    }

    public ListDifference<T> setList2(List<T> list2) {
        this.list2 = list2;
        return this;
    }

    public List<T> getList1Standalone() {
        List<T> standalone = new ArrayList<>(list1);
        standalone.removeAll(list2);
        return standalone;
    }

    public List<T> getList2Standalone() {
        List<T> standalone = new ArrayList<>(list2);
        standalone.removeAll(list1);
        return standalone;
    }

    public List<T> getIntersection() {
        List<T> intersection = new ArrayList<>(list1);
        intersection.retainAll(list2);
        return intersection;
    }

}
