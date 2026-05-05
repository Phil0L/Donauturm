package com.pl.donauturm.spotify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallbackList<C> {

    private final List<C> callbacks;

    public CallbackList() {
        this.callbacks = new ArrayList<>();
    }

    public CallbackList(C callbacks) {
        this.callbacks = new ArrayList<>(Collections.singletonList(callbacks));
    }

    public void add(C callback) {
        if (!callbacks.contains(callback))
            callbacks.add(callback);
    }

    public void remove(C callback) {
        callbacks.remove(callback);
    }

    public void clear(){
        callbacks.clear();
    }

    public void fire(CallbackExecutor<C> executor) {
        for (C callback : callbacks) {
            executor.call(callback);
        }
    }

    public interface CallbackExecutor<C> {
        void call(C callback);
    }
}
