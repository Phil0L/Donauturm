package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.donauturm.drinksmenu.util.json.BitmapDeSerializer;
import com.pl.donauturm.drinksmenu.util.json.PolymorphicDeserializer;
import com.pl.donauturm.pisignageapi.apicontroller.PiSignageAPI;
import com.pl.donauturm.pisignageapi.requests.Request;

import org.jetbrains.annotations.TestOnly;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DrinksMenuCloud extends DrinksMenu {

    public String backgroundUrl;
    public String imageUrl;

    private final transient DrinksMenu original;
    private final transient PiSignageAPI api;

    public DrinksMenuCloud(@NonNull DrinksMenu drinksMenu, @NonNull PiSignageAPI api) {
        super();
        this.original = drinksMenu;
        this.api = api;
        super.name = drinksMenu.name;
        super.items = drinksMenu.items;
        super.height = drinksMenu.height;
        super.width = drinksMenu.width;
        super.version = drinksMenu.version;
        super.backGround = drinksMenu.backGround;
        super.menuImage = drinksMenu.menuImage;
        this.backgroundUrl = Request.PROTOCOL + "://" + Request.HOST + "/media/" + api.getUsername() + "/" + friendly(drinksMenu.name) + "Background.png";
        this.imageUrl = Request.PROTOCOL + "://" + Request.HOST + "/media/" + api.getUsername() + "/" + friendly(name) + ".png";
    }

    @TestOnly
    public DrinksMenuCloud(@NonNull DrinksMenu drinksMenu, @NonNull String username) {
        super();
        this.original = drinksMenu;
        this.api = null;
        super.name = drinksMenu.name;
        super.items = drinksMenu.items;
        super.height = drinksMenu.height;
        super.width = drinksMenu.width;
        super.version = drinksMenu.version;
        super.backGround = drinksMenu.backGround;
        super.menuImage = drinksMenu.menuImage;
        this.backgroundUrl = Request.PROTOCOL + "://" + Request.HOST + "/media/" + username + "/" + friendly(drinksMenu.name) + "Background.png";
        this.imageUrl = Request.PROTOCOL + "://" + Request.HOST + "/media/" + username + "/" + friendly(name) + ".png";
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public DrinksMenu toStandard(Bitmap bg) {
        DrinksMenu drinksMenu = new DrinksMenu();
        drinksMenu.name = name;
        drinksMenu.items = items;
        drinksMenu.height = height;
        drinksMenu.width = width;
        drinksMenu.version = version;
        drinksMenu.backGround = bg;
        return drinksMenu;
    }

    public File bitmapToFile(Bitmap bitmap, Context context, String filename) {
        try {
            File f = new File(context.getCacheDir(), filename);
            boolean created = f.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return f;
        } catch (IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    public void uploadBackGround(Context context) {
        File f = bitmapToFile(original.getBackGround(), context, friendly(name) + "Background.png");
        api.uploadAsset(f).withLabels("generated", "background", "belongs to: " + name).upload();
    }

    public void upload(Context context){
        Bitmap bitmap = original.getMenuImage();
        if (bitmap == null) return;
        uploadBackGround(context);
        File f = bitmapToFile(bitmap, context, friendly(name) + ".png");
        api.uploadAsset(f).withLabels("generated", "drinksmenu", "data:" + serializer().toJson(this)).upload();

    }

    private String friendly(String s){
        return s.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace(" ", "_");
    }

    public static Gson serializer(){
        return new GsonBuilder()
                .registerTypeAdapter(Bitmap.class, BitmapDeSerializer.toNull())
                .create();
    }

    public static Gson deserializer() {
        return new GsonBuilder()
                .registerTypeAdapter(Item.class, new PolymorphicDeserializer<Item>())
                .registerTypeAdapter(Bitmap.class, BitmapDeSerializer.toNull())
                .create();
    }
}
