package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.donauturm.drinksmenu.R;
import com.pl.donauturm.drinksmenu.model.interfaces.Id;
import com.pl.donauturm.drinksmenu.util.json.BitmapDeSerializer;
import com.pl.donauturm.drinksmenu.util.json.PolymorphicDeserializer;

import org.jetbrains.annotations.TestOnly;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("unused")
public class DrinksMenu implements Serializable, Cloneable, Id {

    protected String id;
    protected String name;
    protected List<Item> items;
    protected int width;
    protected int height;
    protected String version;
    protected Bitmap backGround;

    protected transient Bitmap menuImage;

    private transient boolean loading = false;
    private transient CloudState cloudState = CloudState.UNKNOWN;
    private transient List<OnMenuLoadedListener> onMenuLoadedListeners = new ArrayList<>();
    private transient List<OnCloudStateChangedListener> onCloudStateChangedListeners = new ArrayList<>();

    public DrinksMenu() {
        setLoading(true);
    }

    @CreatorConstructor
    private DrinksMenu(String name, Context context) {
        this.name = name;
        this.items = new ArrayList<>();
        this.backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.png_background);
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
        this.version = getVersionNow();
        this.setLoading(false);
    }

    @TestOnly
    public DrinksMenu(String name) {
        this.name = name;
        this.items = new ArrayList<>();
        this.backGround = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888, true);
        this.width = 1920;
        this.height = 1080;
        this.version = getVersionNow();
    }

    @TestOnly
    public DrinksMenu(String name, Item... items) {
        this.name = name;
        this.items = new ArrayList<>(Arrays.asList(items));
        this.backGround = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888, true);
        this.width = 1920;
        this.height = 1080;
        this.version = getVersionNow();
    }

    @TestOnly
    public DrinksMenu(String name, Context context, Item... items) {
        this.name = name;
        this.items = new ArrayList<>(Arrays.asList(items));
        this.backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.png_background);
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
        this.version = getVersionNow();
    }

    @TestOnly
    public DrinksMenu(String name, List<Item> items, Bitmap backGround) {
        this.name = name;
        this.items = new ArrayList<>(items);
        this.width = 1920;
        this.height = (int) (backGround.getHeight() * (1920f / backGround.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(backGround, width, height, true);
        this.version = getVersionNow();
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public Bitmap getBackGround() {
        return backGround;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading() {
        this.loading = true;
    }

    private void setLoading(boolean loading) {
        this.loading = loading;
        if (!loading) {
            if (onMenuLoadedListeners != null) {
                for (OnMenuLoadedListener onMenuLoadedListener : onMenuLoadedListeners) {
                    onMenuLoadedListener.onMenuLoaded(this);
                }
            }
        }
    }

    public void onLoaded(OnMenuLoadedListener listener) {
        if (onMenuLoadedListeners == null)
            this.onMenuLoadedListeners = new ArrayList<>();
        this.onMenuLoadedListeners.add(listener);
    }

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

    public void onCloudStateChanged(OnCloudStateChangedListener listener) {
        if (onCloudStateChangedListeners == null)
            this.onCloudStateChangedListeners = new ArrayList<>();
        this.onCloudStateChangedListeners.add(listener);
        listener.onCloudStateChanged(getCloudState());
    }

    @Nullable
    public Bitmap getMenuImage() {
        return menuImage;
    }

    public void provideMenuImage(Bitmap bitmap) {
        if (bitmap.getWidth() == 1920) {
            this.menuImage = bitmap;
            return;
        }
        this.width = 1920;
        this.height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth())); // keep aspect ratio
        this.menuImage = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public void provideBackGround(Bitmap bitmap) {
        if (bitmap.getWidth() == 1920) {
            this.backGround = bitmap;
            if (loading) setLoading(false);
            return;
        }
        this.width = 1920;
        this.height = (int) (bitmap.getHeight() * (1920f / bitmap.getWidth())); // keep aspect ratio
        this.backGround = Bitmap.createScaledBitmap(bitmap, width, height, true);
        if (loading) setLoading(false);
    }

    public List<Bitmap> getAllBitmaps() {
        List<Bitmap> bitmaps = new ArrayList<>(List.of(menuImage, backGround));
        items.forEach(i -> {
            //TODO: implement
        });
        return bitmaps;
    }

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

    public static Gson serializer(Context context) {
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

    public static Gson serializer() {
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

    public interface OnMenuLoadedListener {
        void onMenuLoaded(DrinksMenu menu);
    }

    @SuppressWarnings("unused")
    public enum CloudState {
        UNKNOWN(R.drawable.ic_cloud, true),
        UP_TO_DATE(R.drawable.ic_cloud_done, true),
        READY_FOR_PUSH(R.drawable.ic_cloud_upload, false),
        PUSHING(R.drawable.ic_cloud_loading, false),
        READY_FOR_PULL(R.drawable.ic_cloud_download, true),
        PULLING(R.drawable.ic_cloud_loading, true),
        NO_CONNECTION(R.drawable.ic_cloud_error, false);

        private final int iconResource;
        private final boolean overwrite;

        public int getIconResource() {
            return iconResource;
        }

        public boolean isAbleToOverwrite() {
            return overwrite;
        }

        CloudState(int iconResource, boolean overwrite) {
            this.iconResource = iconResource;
            this.overwrite = overwrite;
        }
    }

    public interface OnCloudStateChangedListener {
        void onCloudStateChanged(CloudState state);
    }
}
