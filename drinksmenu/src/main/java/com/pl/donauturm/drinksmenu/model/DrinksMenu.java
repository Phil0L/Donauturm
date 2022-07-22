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
import com.pl.donauturm.drinksmenu.util.json.BitmapDeSerializer;
import com.pl.donauturm.drinksmenu.util.json.PolymorphicDeserializer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class DrinksMenu implements Serializable {

    public String name;
    public List<Item> items;

    public int width;
    public int height;

    public String version;

    protected Bitmap backGround;
    @Nullable
    protected transient Bitmap menuImage;

    public DrinksMenu() {
    }

    public DrinksMenu(String name) {
        this.name = name;
        this.items = new ArrayList<>();
        this.backGround = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888, true);
        this.width = 1920;
        this.height = 1080;
        this.version = getVersionNow();
    }

    public DrinksMenu(String name, Item... items) {
        this.name = name;
        this.items = Arrays.asList(items);
        this.backGround = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888, true);
        this.width = 1920;
        this.height = 1080;
        this.version = getVersionNow();
    }

    public DrinksMenu(String name, Context context, Item... items) {
        this.name = name;
        this.items = Arrays.asList(items);
        this.backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.png_background);
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
        this.version = getVersionNow();
    }

    public DrinksMenu(String name, List<Item> items, Bitmap backGround) {
        this.name = name;
        this.items = items;
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
        this.version = getVersionNow();
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
        if (bitmap.getWidth() == 1920){
            this.menuImage = bitmap;
            return;
        }
        this.width = 1920;
        this.height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth())); // keep aspect ratio
        this.menuImage = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public void provideBackGround(Bitmap bitmap){
        if (bitmap.getWidth() == 1920){
            this.backGround = bitmap;
            return;
        }
        this.width = 1920;
        this.height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public List<Bitmap> getAllBitmaps(){
        List<Bitmap> bitmaps = new ArrayList<>(List.of(menuImage, backGround));
        items.forEach(i -> {

        });
        return bitmaps;
    }

    public String getVersion() {
        return version;
    }

    public String increaseVersion(){
        return version = getNewVersion();
    }

    private String getVersionNow() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM", Locale.GERMANY);
        String formattedDate = sdf.format(date);
        return formattedDate + "." + String.format(Locale.GERMANY, "%03d", 1);
    }

    private String getNewVersion() {
        if (version == null) return getVersionNow();
        String month = version.substring(0, version.lastIndexOf("."));
        String v = version.substring(version.lastIndexOf(".") + 1);
        String versionNow = getVersionNow();
        String monthNow = versionNow.substring(0, version.lastIndexOf("."));
        if (month.equals(monthNow)) {
            int vInt = Integer.parseInt(v);
            vInt++;
            v = String.format(Locale.GERMANY, "%03d", vInt);
            return monthNow + "." + v;
        }
        return versionNow;
    }

    /**
     * @param other has to be another version string of the same format as this.version
     * @return true if own version is greater, false if other version is greater or equal
     */
    public boolean isVersionGreaterThan(String other){
        String[] v1 = version.split("\\.");
        String[] v2 = other.split("\\.");
        if (Integer.parseInt(v1[0]) > Integer.parseInt(v2[0])) return true;
        if (Integer.parseInt(v1[0]) < Integer.parseInt(v2[0])) return false;
        if (Integer.parseInt(v1[1]) > Integer.parseInt(v2[1])) return true;
        if (Integer.parseInt(v1[1]) < Integer.parseInt(v2[1])) return false;
        if (Integer.parseInt(v1[2]) > Integer.parseInt(v2[2])) return true;
        if (Integer.parseInt(v1[2]) < Integer.parseInt(v2[2])) return false;
        return false;
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
