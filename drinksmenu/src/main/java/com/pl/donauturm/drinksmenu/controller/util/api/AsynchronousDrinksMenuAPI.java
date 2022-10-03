package com.pl.donauturm.drinksmenu.controller.util.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinkList;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.pisignageapi.apicontroller.AsyncPiSignageAPI;
import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;

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

    public void getAllDrinkMenusIterated(APICallback<DrinksMenu> cb) {
        super.getAllAssets(data -> {
            List<Asset> assets = data.stream().filter(a -> a.getLabels().contains("generated")).collect(Collectors.toList());
            for (Asset asset : assets) {
                AtomicReference<String> gsonData = new AtomicReference<>();
                asset.getLabels().forEach(l -> {
                    if (l.startsWith("data:")) {
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

    public void getAllDrinkMenusIterated(APICallback<DrinksMenu> cb, APICallback<List<DrinksMenu>> cdf) {
        Handler handler = new Handler(Looper.getMainLooper());
        super.getAllAssets(data -> {
            List<Asset> assets = data.stream().filter(a -> a.getLabels().contains("generated")).collect(Collectors.toList());
            List<DrinksMenu> drinksMenus = new ArrayList<>();
            for (Asset asset : assets) {
                AtomicReference<String> gsonData = new AtomicReference<>();
                asset.getLabels().forEach(l -> {
                    if (l.startsWith("data:")) {
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
                if (cb != null)
                    handler.post(() -> cb.onData(drinksMenuCloud));

            }
            if (cdf != null)
                handler.post(() -> cdf.onData(drinksMenus));
        });
    }

    public void getAllDrinkMenusIteratedWithoutImages(APICallback<DrinksMenu> cb, APICallback<List<DrinksMenu>> cdf) {
        Handler handler = new Handler(Looper.getMainLooper());
        super.getAllAssets(data -> {
            List<Asset> assets = data.stream().filter(a -> a.getLabels().contains("generated")).collect(Collectors.toList());
            List<DrinksMenu> drinksMenus = new ArrayList<>();
            for (Asset asset : assets) {
                AtomicReference<String> gsonData = new AtomicReference<>();
                asset.getLabels().forEach(l -> {
                    if (l.startsWith("data:")) {
                        gsonData.set(l.substring(5));
                    }
                });
                if (gsonData.get() == null) continue;
                DrinksMenuCloud drinksMenuCloud = DrinksMenuCloud.deserializer().fromJson(gsonData.get(), DrinksMenuCloud.class);
                if (drinksMenuCloud == null) continue;
                drinksMenus.add(drinksMenuCloud);
                if (cb != null)
                    handler.post(() -> cb.onData(drinksMenuCloud));
            }
            if (cdf != null)
                handler.post(() -> cdf.onData(drinksMenus));
        });
    }

    public void getAllDrinkMenus(APICallback<List<DrinksMenu>> cb) {
        super.getAllAssets(data -> {
            List<Asset> assets = data.stream().filter(a -> a.getLabels().contains("generated")).collect(Collectors.toList());
            List<DrinksMenu> drinksMenus = new ArrayList<>();
            for (Asset asset : assets) {
                AtomicReference<String> gsonData = new AtomicReference<>();
                asset.getLabels().forEach(l -> {
                    if (l.startsWith("data:")) {
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

    public void uploadDrinkMenu(DrinksMenu drinksMenu, AsyncPiSignageAPI.APICallback<Void> cb) {
        new Thread(() -> {
            DrinksMenuCloud cloudDrinksMenu = new DrinksMenuCloud(drinksMenu, synchronous);
            cloudDrinksMenu.upload(context);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (cb != null) cb.onData(null);
            });
        }).start();

    }

    public void getAllDrinks(APICallback<List<Drink>> cb) {
        final String DRINKS_FILENAME = "Drinks";
        getAllNoticeNames(nn -> {
            String noticeName = nn.stream().filter(s -> s.startsWith(DRINKS_FILENAME)).findFirst().orElse(DRINKS_FILENAME);
            getNotice(noticeName, notice -> {
                if (notice == null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (cb != null) cb.onData(null);
                    });
                    return;
                }
                String json = notice.getDescription();
                Gson gson = new Gson();
                DrinkList drinkList = gson.fromJson(json, DrinkList.class);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (cb != null) cb.onData(drinkList.getDrinks());
                });
            });
        });
    }

    public void saveAllDrinks(List<Drink> drinks, APICallback<Void> cb) {
        final String DRINKS_FILENAME = "Drinks";
        getAllNoticeNames(nn -> {
            String oldName = nn.stream().filter(s -> s.startsWith(DRINKS_FILENAME)).findFirst().orElse(DRINKS_FILENAME);
            deleteNotice(oldName, v1 -> {
                Gson gson = new Gson();
                DrinkList drinkList = new DrinkList(drinks);
                String json = gson.toJson(drinkList);
                NoticeUploadMessage num = new NoticeUploadMessage(DRINKS_FILENAME, json, "This is a list of all drinks as a database", "generated");
                createNotice(num, randomFilename ->
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (cb != null) cb.onData(null);
                        }));
            });
        });


    }


}
