package com.pl.donauturm.controller.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class NavigationViewWrapper extends NavigationView {

    private final List<OnNavigationItemSelectedListener> listener;

    public NavigationViewWrapper(@NonNull Context context) {
        super(context);
        this.listener = new ArrayList<>();
        super.setNavigationItemSelectedListener(item -> {
            boolean select = false;
            for (OnNavigationItemSelectedListener listener : listener){
                select = listener.onNavigationItemSelected(item) || select;
            }
            return select;
        });
    }

    public NavigationViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.listener = new ArrayList<>();
        super.setNavigationItemSelectedListener(item -> {
            boolean select = false;
            for (OnNavigationItemSelectedListener listener : listener){
                select = listener.onNavigationItemSelected(item) || select;
            }
            return select;
        });
    }

    public NavigationViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.listener = new ArrayList<>();
        super.setNavigationItemSelectedListener(item -> {
            boolean select = false;
            for (OnNavigationItemSelectedListener listener : listener){
                select = listener.onNavigationItemSelected(item) || select;
            }
            return select;
        });
    }

    @Override
    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        if (listener == null) return;
        addNavigationItemSelectedListener(listener);
    }

    public void addNavigationItemSelectedListener(OnNavigationItemSelectedListener listener){
        this.listener.add(listener);
    }
}
