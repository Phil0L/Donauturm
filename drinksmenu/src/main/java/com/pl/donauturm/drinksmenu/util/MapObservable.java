package com.pl.donauturm.drinksmenu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapObservable<S, T> {

    public List<MapObserver<S, T>> observers = new ArrayList<>();
    private Map<S, T> mapToTrack;

    public void provideMap(Map<S, T> mapToTrack) {
        this.mapToTrack = mapToTrack;
    }

    public void observe(MapObserver<S, T> observer) {
        observers.add(observer);
    }

    public void notifyAddition(int index, S key, T value) {
        observers.forEach(observer -> observer.onAddition(index, key, value, mapToTrack));
    }

    public void notifyAddition(S key, T value) {
        observers.forEach(observer -> observer.onAddition(mapToTrack.size()-1, key, value, mapToTrack));
    }

    public void notifyRemoval(int index, S key, T value) {
        observers.forEach(observer -> observer.onRemoval(index, key, value, mapToTrack));
    }

    public void notifyUpdate(int index, T oldValue, S key, T value) {
        observers.forEach(observer -> observer.onUpdate(index, key, oldValue, value, mapToTrack));
    }


    public interface MapObserver<S, T> {

        void onAddition(int index, S source, T element, Map<S, T> map);

        void onRemoval(int index, S deletedSource, T deletedElement, Map<S, T> map);

        void onUpdate(int index, S source, T oldElement, T newElement, Map<S, T> map);

    }
}
