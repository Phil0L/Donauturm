package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.drinkmenu.DrinksMenuRenderer;
import com.pl.donauturm.drinksmenu.util.BitmapDeSerializer;
import com.pl.donauturm.drinksmenu.util.PolymorphicDeserializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class DrinksMenu implements Serializable {

    public String name;
    public List<Item> items;

    public int width;
    public int height;

    @NonNull
    private Bitmap backGround;
    @Nullable
    private transient Bitmap menuImage;

    public DrinksMenu(String name) {
        this.name = name;
        this.items = new ArrayList<>();
        this.backGround = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888, true);
        this.width = 1920;
        this.height = 1080;
    }

    public DrinksMenu(String name, Item... items) {
        this.name = name;
        this.items = Arrays.asList(items);
        this.backGround = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888, true);
        this.width = 1920;
        this.height = 1080;
    }

    public DrinksMenu(String name, Context context, Item... items) {
        this.name = name;
        this.items = Arrays.asList(items);
        this.backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.png_background);
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
    }

    public DrinksMenu(String name, List<Item> items, Bitmap backGround) {
        this.name = name;
        this.items = items;
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item){
        this.items.add(item);
    }

    public void removeItem(Item item){
        this.items.remove(item);
    }

    @NonNull
    public Bitmap getBackGround() {
        return backGround;
    }

    @Nullable
    public Bitmap getMenuImage() {
        return menuImage;
    }

    @NonNull
    @Deprecated
    public Bitmap requireMenuImage(Context context) {
        if (menuImage != null)
            return menuImage;
        return menuImage = new DrinksMenuRenderer().renderSyncFromMenu(context, this);
    }

    public void provideMenuImage(Bitmap bitmap){
        this.menuImage = bitmap;
    }

    public List<Bitmap> getAllBitmaps(){
        List<Bitmap> bitmaps = new ArrayList<>(List.of(menuImage, backGround));
        items.forEach(i -> {

        });
        return bitmaps;
    }

    @Override
    public boolean equals(Object o) {
       return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, items);
    }

    @NonNull
    @Override
    public String toString() {
        return "DrinksMenu{" +
                "name='" + name + '\'' +
                ", items=" + items +
                '}';
    }

    public static Gson serializer(Context context){
        return new GsonBuilder()
                .registerTypeAdapter(Bitmap.class, BitmapDeSerializer.toLocalFile(context))
                .create();
    }

    public static Gson deserializer(Context context) {
        return new GsonBuilder()
                .registerTypeAdapter(Item.class, new PolymorphicDeserializer<Item>())
                .registerTypeAdapter(Bitmap.class, BitmapDeSerializer.toLocalFile(context))
                .create();
    }
}
