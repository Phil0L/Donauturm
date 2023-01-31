package com.pl.donauturm.drinksmenu.controller.util.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.pl.donauturm.drinksmenu.controller.drinks.routines.GetAllDrinksRoutine;
import com.pl.donauturm.drinksmenu.controller.drinks.routines.SaveAllDrinksRoutine;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.util.BitmapDownloadRequest;
import com.pl.donauturm.pisignageapi.apicontroller.AsyncPiSignageAPI;

import java.util.ArrayList;
import java.util.List;

public class AsynchronousDrinksMenuAPI extends AsyncPiSignageAPI {

    private final Context context;
    public DrinksMenuAPI synchronous;

    protected AsynchronousDrinksMenuAPI(DrinksMenuAPI origin, Context context) {
        super(origin);
        this.synchronous = origin;
        this.context = context;
    }

    public void getAllDrinkMenus(APICallback<DrinksMenu> cb, APICallback<List<DrinksMenu>> cdf) {
        Handler handler = new Handler(Looper.getMainLooper());
        super.getAllAssets(assets -> {
            List<String> jsonList = DrinksMenuCloud.findDMJsonInAssets(assets, synchronous);
            List<DrinksMenu> drinksMenus = new ArrayList<>();
            for (String json : jsonList) {
                if (json == null) continue;
                DrinksMenuCloud drinksMenu = DrinksMenuCloud.deserializer().fromJson(json, DrinksMenuCloud.class);
                if (drinksMenu == null) continue;
                drinksMenu.provideApi(synchronous);
                drinksMenu.requireBackGround(context, null);
                drinksMenu.requireMenuImage(context, null);
                drinksMenus.add(drinksMenu);
                if (cb != null)
                    handler.post(() -> cb.onData(drinksMenu));
            }
            if (cdf != null)
                handler.post(() -> cdf.onData(drinksMenus));
        });
    }

    public void uploadDrinkMenu(DrinksMenu drinksMenu, AsyncPiSignageAPI.APICallback<Void> cb) {
        new Thread(() -> {
            DrinksMenuCloud cloudDrinksMenu = new DrinksMenuCloud(drinksMenu, synchronous);
            cloudDrinksMenu.save(context);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (cb != null) cb.onData(null);
            });
        }).start();
    }

    public void deleteDrinkMenu(DrinksMenu drinksMenu, AsyncPiSignageAPI.APICallback<Void> cb) {
        new Thread(() -> {
            DrinksMenuCloud cloudDrinksMenu = new DrinksMenuCloud(drinksMenu, synchronous);
            cloudDrinksMenu.delete(null);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (cb != null) cb.onData(null);
            });
        }).start();
    }

    public void getAllDrinks(APICallback<List<Drink>> cb) {
        new GetAllDrinksRoutine(synchronous).setCallback(cb::onData).start();
    }

    public void saveAllDrinks(List<Drink> drinks, APICallback<Void> cb) {
        new SaveAllDrinksRoutine(drinks, synchronous).setCallback(cb::onData).start();

    }


    public void getBitmap(String url, APICallback<Bitmap> cb) {
        if (url == null) return;
        new BitmapDownloadRequest(url).requestAsync(cb::onData);
    }
}
