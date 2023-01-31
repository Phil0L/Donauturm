package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class DrinksMenuLocal extends DrinksMenu implements Serializable {

    protected String cloudVersion;
    protected String backgroundName;
    protected String imageName;
    protected String dataName;

    @Keep // for Gson parsing
    @SuppressWarnings("unused")
    public DrinksMenuLocal() {
        super();
    }

    public DrinksMenuLocal(@NonNull DrinksMenu drinksMenu) {
        super(drinksMenu);
        this.backgroundName = friendly(id) + "B.png";
        this.imageName = friendly(id) + ".png";
        this.dataName = friendly(id) + ".json";
    }

    public String getCloudVersion() {
        return cloudVersion;
    }

    public void setCloudVersion(String cloudVersion) {
        if (cloudVersion == null)
            return;
        this.cloudVersion = cloudVersion;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getDataName() {
        return dataName;
    }

    @Override
    public void save(Context context) {
        dataToFile(serializer().toJson(this), context, directory(name), dataName);
        if (getBackGround() == null) return;
        bitmapToFile(getBackGround(), context, directory(name), backgroundName);
        if (getMenuImage() == null) return;
        bitmapToFile(getMenuImage(), context, directory(name), imageName);

    }

    @Override
    public void delete(Context context) {
        deleteFile(context, directory(name), dataName);
        deleteFile(context, directory(name), backgroundName);
        deleteFile(context, directory(name), imageName);
    }

    @Override
    public void requireMenuImage(Context context, OnBitmapLoadedListener callback) {
        Bitmap image = requireMenuImage(context);
        if (image != null)
            callback.onBitmapLoaded(image);
    }

    public Bitmap requireMenuImage(Context context) {
        if (name == null) return null;
        File dmDir = new File(context.getFilesDir(), directory(name));
        if (!dmDir.exists()) return null;
        if (getImageName() == null) return null;
        File bgFile = new File(dmDir, getImageName());
        if (!bgFile.exists()) return null;
        Bitmap bg = BitmapFactory.decodeFile(bgFile.getAbsolutePath());
        provideBackGround(bg);
        return bg;
    }

    @Override
    public void requireBackGround(@NonNull Context context, OnBitmapLoadedListener callback) {
        Bitmap background = requireBackGround(context);
        if (background != null && callback != null)
            callback.onBitmapLoaded(background);
    }

    public Bitmap requireBackGround(@NonNull Context context) {
        if (name == null) return null;
        File dmDir = new File(context.getFilesDir(), directory(name));
        if (!dmDir.exists()) return null;
        if (getBackgroundName() == null) return null;
        File bgFile = new File(dmDir, getBackgroundName());
        if (!bgFile.exists()) return null;
        Bitmap bg = BitmapFactory.decodeFile(bgFile.getAbsolutePath());
        provideBackGround(bg);
        return bg;
    }

    /**
     * @param other has to be another version string of the same format as this.version
     * @return true if own cloud version is greater, false if other cloud version is greater or equal
     */
    public boolean hasGreaterCloudVersionThan(String other) {
        if (cloudVersion == null || cloudVersion.isEmpty()) return false;
        if (other == null || other.isEmpty()) return true;
        String[] self = cloudVersion.split("\\.");
        String[] some = other.split("\\.");
        if (Integer.parseInt(self[0]) > Integer.parseInt(some[0])) return true;
        if (Integer.parseInt(self[0]) < Integer.parseInt(some[0])) return false;
        if (Integer.parseInt(self[1]) > Integer.parseInt(some[1])) return true;
        if (Integer.parseInt(self[1]) < Integer.parseInt(some[1])) return false;
        return Integer.parseInt(self[2]) > Integer.parseInt(some[2]);
    }

    // HELPERS //

    public static String friendly(String s) {
        return s.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace(" ", "_");
    }

    public static String mainDir() {
        return "drinksmenu";
    }

    public static String directory(String s) {
        return mainDir() + File.separator + friendly(s);
    }

    @Nullable
    public static File findDataFileInDir(@NonNull File dir){
        File[] array = dir.listFiles((file, s) -> s.endsWith(".json"));
        if (array == null || array.length == 0) return null;
        return array[0];
    }

    private void bitmapToFile(Bitmap bitmap, Context context, String dir, String filename) {
        try {
            File fDir = new File(context.getFilesDir(), dir);
            boolean createdDirs = fDir.mkdirs();
            File f = new File(fDir, filename);
            boolean createdFile = f.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void dataToFile(String data, Context context, String dir, String filename) {
        try {
            File fDir = new File(context.getFilesDir(), dir);
            if (fDir.mkdirs()) Log.d(getClass().getSimpleName(), "Created new directory " + fDir.getPath());
            File f = new File(fDir, filename);
            if (f.createNewFile()) Log.d(getClass().getSimpleName(), "Created new file " + f.getPath());

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void deleteFile(Context context, String dir, String filename){
        File fDir = new File(context.getFilesDir(), dir);
        File f = new File(fDir, filename);
        if (!f.delete()) Log.d(getClass().getSimpleName(), "Tried to delete local save, but it is non-existent already: " + f.getPath());
    }


}
