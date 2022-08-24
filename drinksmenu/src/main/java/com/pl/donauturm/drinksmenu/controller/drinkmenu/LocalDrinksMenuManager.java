package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import static com.pl.donauturm.drinksmenu.model.DrinksMenuLocal.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenuLocal.friendly;
import static com.pl.donauturm.drinksmenu.model.DrinksMenuLocal.mainDir;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuLocal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class LocalDrinksMenuManager {

    private static final Set<String> LOCAL_DRINKS_MENU_FILES = new HashSet<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadAllLocalSaves(Context context) {
        File dmDir = new File(context.getFilesDir(), mainDir());
        if (!dmDir.exists()) return;
        String[] dmDirs = dmDir.list();
        if (dmDirs == null) return;
        for (String dmDirName : dmDirs) {
            File dmDirFile = new File(dmDir, dmDirName);
            if (!dmDirFile.exists()) continue;
            try {
                // load the data
                File dataFile = new File(dmDirFile, friendly(dmDirName) + ".json");
                if (!dataFile.exists()) throw new IOException("data file not found");
                String json = new String(Files.readAllBytes(dataFile.toPath()));
                if (json.isEmpty()) throw new IOException("data file empty");
                DrinksMenuLocal dm = deserializer().fromJson(json, DrinksMenuLocal.class);
                if (dm == null) throw new IOException("data file empty");

                // load the background
                File bgFile = new File(dmDirFile, friendly(dmDirName) + "Background.png");
                if (!bgFile.exists()) throw new IOException("background file not found");
                Bitmap bg = BitmapFactory.decodeFile(bgFile.getAbsolutePath());
                if (bg == null) throw new IOException("background file empty");
                dm.provideBackGround(bg);

                // load the image
                File imgFile = new File(dmDirFile, friendly(dmDirName) + ".png");
                if (imgFile.exists()) {
                    Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (img != null) dm.provideMenuImage(img);
                }

                // add the menu to the list
                LOCAL_DRINKS_MENU_FILES.add(dm.getName());
                DrinkMenuRegistry.getInstance().put(dm.getName(), dm);

            } catch (IOException e) {
                e.printStackTrace();
                dmDirFile.delete();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadAllLocalSavesAsync(Context context) {
        new Thread(() -> {
            File dmDir = new File(context.getFilesDir(), mainDir());
            if (!dmDir.exists()) return;
            String[] dmDirs = dmDir.list();
            if (dmDirs == null) return;
            for (String dmDirName : dmDirs) {
                File dmDirFile = new File(dmDir, dmDirName);
                if (!dmDirFile.exists()) continue;
                try {
                    // load the data
                    File dataFile = new File(dmDirFile, friendly(dmDirName) + ".json");
                    if (!dataFile.exists()) throw new IOException("data file not found");
                    String json = new String(Files.readAllBytes(dataFile.toPath()));
                    if (json.isEmpty()) throw new IOException("data file empty");
                    DrinksMenuLocal dm = deserializer().fromJson(json, DrinksMenuLocal.class);
                    if (dm == null) throw new IOException("data file empty");

                    // load the background
                    File bgFile = new File(dmDirFile, friendly(dmDirName) + "Background.png");
                    if (!bgFile.exists()) throw new IOException("background file not found");
                    Bitmap bg = BitmapFactory.decodeFile(bgFile.getAbsolutePath());
                    if (bg == null) throw new IOException("background file empty");
                    dm.provideBackGround(bg);

                    // load the image
                    File imgFile = new File(dmDirFile, friendly(dmDirName) + ".png");
                    if (imgFile.exists()) {
                        Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (img != null) dm.provideMenuImage(img);
                    }

                    // add the menu to the list
                    new Handler(Looper.getMainLooper()).post(() -> {
                        LOCAL_DRINKS_MENU_FILES.add(dm.getName());
                        DrinkMenuRegistry.getInstance().put(dm.getName(), dm);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    dmDirFile.delete();
                }
            }
        }).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadLocalSave(Context context, String name) {
        File dmDir = new File(context.getFilesDir(), mainDir());
        if (!dmDir.exists()) return;
        File dmDirFile = new File(dmDir, name);
        if (!dmDirFile.exists()) return;
        try {
            // load the data
            File dataFile = new File(dmDirFile, friendly(name) + ".json");
            if (!dataFile.exists()) throw new IOException("data file not found");
            String json = new String(Files.readAllBytes(dataFile.toPath()));
            if (json.isEmpty()) throw new IOException("data file empty");
            DrinksMenuLocal dm = deserializer().fromJson(json, DrinksMenuLocal.class);
            if (dm == null) throw new IOException("data file empty");

            // load the background
            File bgFile = new File(dmDirFile, friendly(name) + "Background.png");
            if (!bgFile.exists()) throw new IOException("background file not found");
            Bitmap bg = BitmapFactory.decodeFile(bgFile.getAbsolutePath());
            if (bg == null) throw new IOException("background file empty");
            dm.provideBackGround(bg);

            // load the image
            File imgFile = new File(dmDirFile, friendly(name) + ".png");
            if (imgFile.exists()) {
                Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (img != null) dm.provideMenuImage(img);
            }

            // add the menu to the list
            LOCAL_DRINKS_MENU_FILES.add(dm.getName());
            DrinkMenuRegistry.getInstance().put(dm.getName(), dm);
        } catch (IOException e) {
            e.printStackTrace();
            dmDirFile.delete();
        }

    }

    public static void deleteAllLocalSaves(Context context) {
        File dmDir = new File(context.getFilesDir(), mainDir());
        if (!dmDir.exists()) return;
        String[] dmDirs = dmDir.list();
        if (dmDirs == null) return;
        for (String dmDirName : dmDirs) {
            File dmDirFile = new File(dmDir, dmDirName);
            if (!dmDirFile.exists()) continue;
            deleteDirectory(dmDirFile);
        }
    }

    public static void deleteLocalSave(Context context, String name) {
        File dmDir = new File(context.getFilesDir(), mainDir());
        if (!dmDir.exists()) return;
        File dmDirFile = new File(dmDir, name);
        if (!dmDirFile.exists()) return;
        deleteDirectory(dmDirFile);
    }

    public static Set<String> getLoadedLocalDrinksMenuNames() {
        return LOCAL_DRINKS_MENU_FILES;
    }

    public static boolean containsLocalSave(Context context, String name) {
        if (name == null) return false;
        if (LOCAL_DRINKS_MENU_FILES.contains(name)) return true;
        File dmDir = new File(context.getFilesDir(), mainDir());
        if (!dmDir.exists()) return false;
        File dmDirFile = new File(dmDir, name);
        return dmDirFile.exists();
    }

    public static void saveLocalSave(Context context, DrinksMenu drinksMenu) {
        if (context == null || drinksMenu == null) return;
        if (drinksMenu instanceof DrinksMenuLocal) {
            ((DrinksMenuLocal) drinksMenu).save(context);
            return;
        }
        new DrinksMenuLocal(drinksMenu).save(context);
    }

    public static void saveLocalSaveAsync(Context context, DrinksMenu drinksMenu) {
        new Thread(() -> saveLocalSave(context, drinksMenu)).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
