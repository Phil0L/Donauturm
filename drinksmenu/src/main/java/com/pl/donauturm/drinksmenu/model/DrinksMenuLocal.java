package com.pl.donauturm.drinksmenu.model;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pl.donauturm.drinksmenu.util.json.BitmapDeSerializer;
import com.pl.donauturm.drinksmenu.util.json.PolymorphicDeserializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrinksMenuLocal extends DrinksMenu {

    public String cloudVersion;
    public String backgroundPath;
    public String imagePath;
    public String dataPath;

    private final transient DrinksMenu original;

    public DrinksMenuLocal(@NonNull DrinksMenu drinksMenu) {
        super();
        this.original = drinksMenu;
        super.name = drinksMenu.name;
        super.items = drinksMenu.items;
        super.height = drinksMenu.height;
        super.width = drinksMenu.width;
        super.version = drinksMenu.version;
        super.backGround = drinksMenu.backGround;
        super.menuImage = drinksMenu.menuImage;
    }

    public String getCloudVersion() {
        return cloudVersion;
    }

    public void setCloudVersion(String cloudVersion) {
        this.cloudVersion = cloudVersion;
    }

    public File bitmapToFile(Bitmap bitmap, Context context, String dir, String filename) {
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
            return f;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public File dataToFile(String data, Context context, String dir, String filename) {
        try {
            File fDir = new File(context.getFilesDir(), dir);
            boolean createdDirs = fDir.mkdirs();
            File f = new File(fDir, filename);
            boolean createdFile = f.createNewFile();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
            return f;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public void save(Context context) {
        File data = dataToFile(serializer().toJson(this), context, directory(name), friendly(name) + ".json");
        File bg = bitmapToFile(getBackGround(), context, directory(name), friendly(name) + "Background.png");
        Bitmap bitmap = getMenuImage();
        if (bitmap != null) {
            File img = bitmapToFile(bitmap, context, directory(name), friendly(name) + ".png");
        }
    }

    /**
     * @param other has to be another version string of the same format as this.version
     * @return true if own cloud version is greater, false if other cloud version is greater or equal
     */
    public boolean hasGreaterCloudVersionThan(String other) {
        if (cloudVersion == null || cloudVersion.isEmpty()) return true;
        if (other == null || other.isEmpty()) return false;
        String[] self = cloudVersion.split("\\.");
        String[] some = other.split("\\.");
        if (Integer.parseInt(self[0]) > Integer.parseInt(some[0])) return true;
        if (Integer.parseInt(self[0]) < Integer.parseInt(some[0])) return false;
        if (Integer.parseInt(self[1]) > Integer.parseInt(some[1])) return true;
        if (Integer.parseInt(self[1]) < Integer.parseInt(some[1])) return false;
        if (Integer.parseInt(self[2]) > Integer.parseInt(some[2])) return true;
        if (Integer.parseInt(self[2]) < Integer.parseInt(some[2])) return false;
        return false;
    }

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

    private String directory(String s) {
        return mainDir() + File.separator + friendly(s);
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


}
