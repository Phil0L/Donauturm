package com.pl.donauturm.drinksmenu.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.pisignageapi.apicontroller.AsyncPiSignageAPI;
import com.pl.donauturm.pisignageapi.model.Asset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AsynchronousDrinksMenuAPI extends AsyncPiSignageAPI {

    private final Context context;
    public DrinksMenuAPI synchronous;

    protected AsynchronousDrinksMenuAPI(DrinksMenuAPI origin, Context context) {
        super(origin);
        this.synchronous = origin;
        this.context = context;
    }

    public void getAllDrinkMenusIterated(APICallback<DrinksMenu> cb){
        if (!isLoggedIn()){
            login((v) -> getAllDrinkMenusIterated(cb));
            return;
        }
        super.getAllAssets(data -> {
            List<Asset> assets = data.stream().filter(a -> a.getLabels().contains("generated")).collect(Collectors.toList());
            for (Asset asset : assets){
                AtomicReference<String> gsonData = new AtomicReference<>();
                asset.getLabels().forEach(l -> {
                    if (l.startsWith("data:")){
                        gsonData.set(l.substring(5));
                    }
                });
                if (gsonData.get() == null) continue;
                DrinksMenuCloud drinksMenuCloud = DrinksMenuCloud.deserializer().fromJson(gsonData.get(), DrinksMenuCloud.class);
                if (drinksMenuCloud == null) continue;
                Bitmap background = synchronous.getBitmap(drinksMenuCloud.getBackgroundUrl());
                Bitmap image = synchronous.getBitmap(drinksMenuCloud.getImageUrl());
                drinksMenuCloud.provideMenuImage(image);
                drinksMenuCloud.provideBackGround(background);
                if (cb != null) cb.onData(drinksMenuCloud);
            }
        });
    }

    public void getAllDrinkMenus(APICallback<List<DrinksMenu>> cb){
        if (!isLoggedIn()){
            login((v) -> getAllDrinkMenus(cb));
            return;
        }
        super.getAllAssets(data -> {
            List<Asset> assets = data.stream().filter(a -> a.getLabels().contains("generated")).collect(Collectors.toList());
            List<DrinksMenu> drinksMenus = new ArrayList<>();
            for (Asset asset : assets){
                AtomicReference<String> gsonData = new AtomicReference<>();
                asset.getLabels().forEach(l -> {
                    if (l.startsWith("data:")){
                        gsonData.set(l.substring(5));
                    }
                });
                if (gsonData.get() == null) continue;
                DrinksMenuCloud drinksMenuCloud = DrinksMenuCloud.deserializer().fromJson(gsonData.get(), DrinksMenuCloud.class);
                if (drinksMenuCloud == null) continue;
                Bitmap background = synchronous.getBitmap(drinksMenuCloud.getBackgroundUrl());
                Bitmap image = synchronous.getBitmap(drinksMenuCloud.getImageUrl());
                drinksMenuCloud.provideMenuImage(image);
                drinksMenuCloud.provideBackGround(background);
                drinksMenus.add(drinksMenuCloud);
            }
            if (cb != null) cb.onData(drinksMenus);
        });
    }

    public void uploadDrinkMenu(DrinksMenu drinksMenu, AsyncPiSignageAPI.APICallback<Void> cb){
        new Thread(() -> {
            DrinksMenuCloud cloudDrinksMenu = new DrinksMenuCloud(drinksMenu, synchronous);
            cloudDrinksMenu.upload(context);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (cb != null) cb.onData(null);
            });
        }).start();

    }
}
