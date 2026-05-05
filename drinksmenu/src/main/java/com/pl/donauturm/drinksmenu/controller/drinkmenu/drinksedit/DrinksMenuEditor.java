package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuRenderer;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;

public class DrinksMenuEditor extends DrinksMenu {


    // CONSTRUCTING //

    @Keep // for Gson parsing
    @SuppressWarnings("unused")
    public DrinksMenuEditor() {
        super();
    }

    public DrinksMenuEditor(@NonNull DrinksMenu source) {
        super(source);
    }

    // MANAGING //

    public void addItem(DrinksMenuItem item) {
        super.items.add(item);
    }

    public void removeItem(DrinksMenuItem item) {
        super.items.remove(item);
    }

    @Override
    public void save(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requireMenuImage(Context context, OnBitmapLoadedListener callback) {
        if (callback != null)
            render(context, callback);
        else
            render(context);
    }

    private void render(Context context) {
        render(context, b -> {});
    }

    private void render(Context context, OnBitmapLoadedListener callback) {
        if (!(context instanceof DrinksMenuEditorActivity)) return;
        DrinksMenuEditorActivity dea = (DrinksMenuEditorActivity) context;
        Handler handler = new Handler(Looper.getMainLooper());
        new DrinksMenuRenderer().renderAsyncFromView(dea.getBinding().previewHolder, bm -> {
            provideMenuImage(bm);
            if (getMenuImage() != null)
                handler.post(() -> callback.onBitmapLoaded(getMenuImage()));
        });
    }

    @Override
    public void requireBackGround(@NonNull Context context, OnBitmapLoadedListener callback) {
        callback.onBitmapLoaded(getBackGround());
    }
}
