package com.pl.donauturm.drinksmenu.controller.util.api;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.pl.donauturm.drinksmenu.model.Drink;
import com.pl.donauturm.drinksmenu.model.DrinkList;
import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.util.BitmapDownloadRequest;
import com.pl.donauturm.pisignageapi.apicontroller.PiSignageAPI;
import com.pl.donauturm.pisignageapi.model.Asset;
import com.pl.donauturm.pisignageapi.model.Notice;
import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DrinksMenuAPI extends PiSignageAPI {

    private final Context context;
    public AsynchronousDrinksMenuAPI asynchronous;

    DrinksMenuAPI(String username, String password, Context context) {
        super(username, password);
        this.asynchronous = new AsynchronousDrinksMenuAPI(this, context);
        this.context = context;
    }

    public static DrinksMenuAPI simple(String username, String password, Context context) {
        return new DrinksMenuAPI(username, password, context);
    }

    public List<DrinksMenu> getAllDrinkMenus(){
        List<Asset> data = super.getAllAssets();
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
            Bitmap background = getBitmap(drinksMenuCloud.getBackgroundUrl());
            Bitmap image = getBitmap(drinksMenuCloud.getImageUrl());
            drinksMenuCloud.provideMenuImage(image);
            drinksMenuCloud.provideBackGround(background);
            drinksMenus.add(drinksMenuCloud);
        }
        return drinksMenus;
    }

    public Bitmap getBitmap(String url) {
        if (url == null) return null;
        return new BitmapDownloadRequest(url).request();
    }

    public List<Drink> getAllDrinks(){
        final String DRINKS_FILENAME = "Drinks";
        Notice notice = getNotice(DRINKS_FILENAME);
        if (notice == null) return null;
        String json = notice.getDescription();
        Gson gson = new Gson();
        DrinkList drinkList = gson.fromJson(json, DrinkList.class);
        return drinkList.getDrinks();
    }

    public void saveAllDrinks(List<Drink> drinks){
        final String DRINKS_FILENAME = "Drinks";
        Gson gson = new Gson();
        DrinkList drinkList = new DrinkList(drinks);
        String json = gson.toJson(drinkList);
        deleteNotice(DRINKS_FILENAME);
        NoticeUploadMessage num = new NoticeUploadMessage("All Drinks", json, "This is a list of all drinks as a database", "generated");
        String randomFilename = createNotice(num);
        renameNotice(randomFilename, DRINKS_FILENAME);
    }

}
