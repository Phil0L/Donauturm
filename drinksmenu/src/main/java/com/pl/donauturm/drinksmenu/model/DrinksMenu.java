package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.controller.util.CloudState;
import com.pl.donauturm.drinksmenu.controller.util.CloudState.OnCloudStateChangedListener;
import com.pl.donauturm.drinksmenu.model.content.DrinkGroupItem;
import com.pl.donauturm.drinksmenu.model.content.DrinkItem;
import com.pl.donauturm.drinksmenu.model.content.DrinksMenuItem;
import com.pl.donauturm.drinksmenu.model.interfaces.Id;
import com.pl.donauturm.drinksmenu.model.interfaces.NotYetImplemented;
import com.pl.donauturm.drinksmenu.util.json.PolymorphicDeserializer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public abstract class DrinksMenu implements Serializable, Cloneable, Id {

    // ATTRIBUTES //

    protected String id;
    protected String name;
    protected List<DrinksMenuItem> items;
    protected int width;
    protected int height;
    protected String version;

    protected transient Bitmap backGround;
    protected transient Bitmap menuImage;

    private transient CloudState cloudState = CloudState.UNKNOWN;
    private transient List<OnMenuLoadedListener> onMenuLoadedListeners = new ArrayList<>();
    private transient List<OnCloudStateChangedListener> onCloudStateChangedListeners = new ArrayList<>();

    // CONSTRUCTORS //

    public DrinksMenu() {
        this.createNewIdIfNull(id);
    }

    protected DrinksMenu(DrinksMenu source){
        this.id = source.id;
        this.name = source.name;
        this.items = new ArrayList<>(source.items);
        this.width = source.width;
        this.height = source.height;
        this.version = source.version;

        this.cloudState = source.cloudState;

        if (source.hasBackGroundLoaded())
            this.backGround = source.backGround;
        if (source.hasMenuImageLoaded())
            this.menuImage = source.menuImage;
    }

    public static DrinksMenu createNew(Context context, String name){
        DrinksMenu drinksMenu = new DrinksMenuCreator();
        drinksMenu.createNewId();
        drinksMenu.name = name;
        drinksMenu.items = new ArrayList<>();
        drinksMenu.backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.png_background);
        drinksMenu.width = 1920;
        drinksMenu.height = (int) (drinksMenu.backGround.getHeight() * (1920f / drinksMenu.backGround.getWidth())); // keep aspect ratio
        drinksMenu.backGround = Bitmap.createScaledBitmap(drinksMenu.backGround, drinksMenu.width, drinksMenu.height, true);
        drinksMenu.version = drinksMenu.getVersionNow();
        return drinksMenu;
    }

    public static class DrinksMenuCreator extends DrinksMenu{
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void requireBackGround(@NonNull Context context, OnBitmapLoadedListener callback) {
            throw new UnsupportedOperationException();
        }
    }

    // GETTERS & SETTERS //

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public void rename(String newName){
        setName(newName);
    }

    public List<DrinksMenuItem> getItems() {
        return items;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // LOADING //

    public void invalidateLoadedState(){
        if (!isLoading()){
            fireLoadedListeners();
        }
    }

    private void fireLoadedListeners() {
        if (onMenuLoadedListeners != null) {
            for (OnMenuLoadedListener onMenuLoadedListener : onMenuLoadedListeners) {
                onMenuLoadedListener.onMenuLoaded(this);
            }
        }
        onMenuLoadedListeners = new ArrayList<>();
    }

    public boolean isLoading() {
        return !(hasBackGroundLoaded() && hasMenuImageLoaded());
    }

    public void addOnLoadedListener(OnMenuLoadedListener listener) {
        if (!isLoading()){
            listener.onMenuLoaded(this);
            return;
        }
        if (onMenuLoadedListeners == null)
            this.onMenuLoadedListeners = new ArrayList<>();
        this.onMenuLoadedListeners.add(listener);
    }

    // SAVING //

    public abstract void save(Context context);

    public abstract void delete(Context context);

    // CLOUD STATE //

    public CloudState getCloudState() {
        return cloudState;
    }

    public void setCloudState(CloudState cloudState) {
        this.cloudState = cloudState;
        if (onCloudStateChangedListeners != null) {
            for (OnCloudStateChangedListener onCloudStateChangedListener : onCloudStateChangedListeners) {
                onCloudStateChangedListener.onCloudStateChanged(cloudState);
            }
        }
    }

    public void addOnCloudStateChangedListener(OnCloudStateChangedListener listener) {
        if (onCloudStateChangedListeners == null)
            this.onCloudStateChangedListeners = new ArrayList<>();
        this.onCloudStateChangedListeners.add(listener);
        listener.onCloudStateChanged(getCloudState());
    }

    // IMAGES //

    @Nullable
    public Bitmap getMenuImage() {
        return menuImage;
    }

    public boolean hasMenuImageLoaded(){
        return menuImage != null;
    }

    public abstract void requireMenuImage(Context context, OnBitmapLoadedListener callback);

    public void provideMenuImage(Bitmap bitmap) {
        if (bitmap.getWidth() == 1920) {
            this.menuImage = bitmap;
            this.invalidateLoadedState();
            return;
        }
        if (aspectRatioSucks(bitmap)) return;
        this.width = 1920;
        this.height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth())); // keep aspect ratio
        this.menuImage = Bitmap.createScaledBitmap(bitmap, width, height, true);
        this.invalidateLoadedState();
    }

    @Nullable
    public Bitmap getBackGround() {
        return backGround;
    }

    public boolean hasBackGroundLoaded(){
        return backGround != null;
    }

    public abstract void requireBackGround(@NonNull Context context, OnBitmapLoadedListener callback);

    public void provideBackGround(Bitmap bitmap) {
        if (bitmap == null) return;
        if (bitmap.getWidth() == 1920) {
            this.backGround = bitmap;
            this.invalidateLoadedState();
            return;
        }
        if (aspectRatioSucks(bitmap)) return;
        this.width = 1920;
        this.height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(bitmap, width, height, true);
        this.invalidateLoadedState();
    }

    private boolean aspectRatioSucks(Bitmap bitmap) {
        if (bitmap.getWidth() == 1920) return false;
        if (bitmap.getHeight() > bitmap.getWidth()) return true;
        int width = 1920;
        int height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth()));
        return width / height >= 3;
    }

    public List<DrinkItem> getAllDrinks() {
        List<DrinkItem> drinks = new ArrayList<>();
        items.forEach(i -> {
            if (i instanceof DrinkItem) drinks.add((DrinkItem) i);
            else if (i instanceof DrinkGroupItem) drinks.addAll(((DrinkGroupItem) i).getItems());
        });
        return drinks;
    }

    @SuppressWarnings("unused")
    @NotYetImplemented(cause = "No images in DMs yet")
    public List<Bitmap> getAllBitmaps() {
        List<Bitmap> bitmaps = new ArrayList<>(List.of(menuImage, backGround));
        items.forEach(i -> {
            //TODO: implement
        });
        return bitmaps;
    }

    // VERSION //

    public String getVersion() {
        return version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public void increaseVersion() {
        version = getNewVersion();
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
    public boolean hasGreaterVersionThan(String other) {
        if (version == null || version.isEmpty()) return true;
        if (other == null || other.isEmpty()) return false;
        String[] self = version.split("\\.");
        String[] some = other.split("\\.");
        if (Integer.parseInt(self[0]) > Integer.parseInt(some[0])) return true;
        if (Integer.parseInt(self[0]) < Integer.parseInt(some[0])) return false;
        if (Integer.parseInt(self[1]) > Integer.parseInt(some[1])) return true;
        if (Integer.parseInt(self[1]) < Integer.parseInt(some[1])) return false;
        return Integer.parseInt(self[2]) > Integer.parseInt(some[2]);
    }


    // CLONING //

    @Override
    @NonNull
    @SuppressWarnings("UnusedAssignment")
    public DrinksMenu clone() {
        try {
            DrinksMenu clone = (DrinksMenu) super.clone();
            String content = serializer().toJson(this);
            clone = deserializer().fromJson(content, DrinksMenu.class);
            clone.createNewId();
            clone.provideBackGround(Bitmap.createBitmap(backGround));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @NonNull
    @SuppressWarnings("UnusedAssignment")
    public DrinksMenu clone(String name) {
        try {
            DrinksMenu clone = (DrinksMenu) super.clone();
            String content = serializer().toJson(this);
            clone = deserializer().fromJson(content, DrinksMenu.class);
            clone.createNewId();
            clone.provideBackGround(Bitmap.createBitmap(backGround));
            clone.setVersion(getVersionNow());
            clone.setName(name);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    // IDS //

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getIdLong() {
        return Long.parseLong(id);
    }

    @Override
    public DrinksMenu createNewId() {
        this.id = newId();
        return this;
    }

    // DEFAULT METHODS //

    @Override
    public boolean equals(Object o) {
        return o instanceof DrinksMenu && ((DrinksMenu) o).getIdLong() == getIdLong();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, items);
    }

    @NonNull
    @Override
    public String toString() {
        return "DrinksMenu{" +
                "name='" + name + '\'' +
                "version='" + version + '\'' +
                ", items=" + items +
                ",width=" + width +
                ",height=" + height +
                '}';
    }

    // PARCELABLE //

    public static Gson serializer() {
        return new GsonBuilder().create();
    }

    public static Gson deserializer() {
        return new GsonBuilder()
                .registerTypeAdapter(DrinksMenuItem.class, new PolymorphicDeserializer<DrinksMenuItem>())
                .create();
    }

    // INTERFACES //

    public interface OnMenuLoadedListener {
        void onMenuLoaded(DrinksMenu menu);
    }

    public interface OnBitmapLoadedListener {
        void onBitmapLoaded(Bitmap... bitmaps);
    }


}
