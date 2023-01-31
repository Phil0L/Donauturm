package com.pl.donauturm.drinksmenu.controller.drinkmenu;

import static com.pl.donauturm.drinksmenu.model.DrinksMenuLocal.deserializer;
import static com.pl.donauturm.drinksmenu.model.DrinksMenuLocal.directory;
import static com.pl.donauturm.drinksmenu.model.DrinksMenuLocal.mainDir;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;
import com.pl.donauturm.drinksmenu.model.DrinksMenuCloud;
import com.pl.donauturm.drinksmenu.model.DrinksMenuLocal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class LocalDrinksMenuManager {

    private static final Set<String> LOCAL_DRINKS_MENU_FILES = new HashSet<>();

    public static void loadAllLocalSaves(Context context) {
        File dmDir = new File(context.getFilesDir(), mainDir());
        if (!dmDir.exists()) return;
        String[] dmDirs = dmDir.list();
        if (dmDirs == null) return;
        for (String dmDirName : dmDirs) {
            loadLocalSave(context, dmDirName);
        }
    }

    public static void loadAllLocalSavesAsync(Context context) {
        new Thread(() -> loadAllLocalSaves(context)).start();
    }

    public static void loadLocalSave(Context context, String name) {
        File dmDir = new File(context.getFilesDir(), directory(name));
        if (!dmDir.exists()) return;
        try {
            // load the data
            File dataFile = DrinksMenuLocal.findDataFileInDir(dmDir);
            if (dataFile == null || !dataFile.exists()) throw new IOException("data file not found");
            String json = new String(Files.readAllBytes(dataFile.toPath()));
            if (json.isEmpty()) throw new IOException("data file empty");
            DrinksMenuLocal dm = deserializer().fromJson(json, DrinksMenuLocal.class);
            if (dm == null) throw new IOException("data file not parsable");

            // load the images
            dm.requireBackGround(context);
            dm.requireMenuImage(context);

            // add the menu to the list
            LOCAL_DRINKS_MENU_FILES.add(dm.getName());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> DrinksMenuRegistry.getInstance().put(dm));
        } catch (IOException e) {
            e.printStackTrace();
            deleteDirectory(dmDir);
        }

    }

    @Deprecated
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

    public static Set<String> getLoadedLocalSaves() {
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

    public static void saveLocalSave(@NonNull Context context, @NonNull DrinksMenu drinksMenu) {
        if (drinksMenu instanceof DrinksMenuLocal) {
            drinksMenu.save(context);
            return;
        }
        if (drinksMenu instanceof DrinksMenuCloud) {
            DrinksMenuLocal drinksMenuLocal = new DrinksMenuLocal(drinksMenu);
            drinksMenuLocal.setCloudVersion(drinksMenu.getVersion());
            drinksMenuLocal.save(context);
            return;
        }
        DrinksMenuLocal drinksMenuLocal = new DrinksMenuLocal(drinksMenu);
        drinksMenuLocal.save(context);
    }

    public static void saveLocalAsync(@NonNull Context context, @NonNull DrinksMenu drinksMenu){
        DrinksMenuLocal drinksMenuLocal;
        if (drinksMenu instanceof DrinksMenuLocal) {
            drinksMenuLocal = (DrinksMenuLocal) drinksMenu;
        }else if (drinksMenu instanceof DrinksMenuCloud) {
            drinksMenuLocal = new DrinksMenuLocal(drinksMenu);
            drinksMenuLocal.setCloudVersion(drinksMenu.getVersion());
        }else {
            drinksMenuLocal = new DrinksMenuLocal(drinksMenu);
        }
        new Thread(() -> saveLocalSave(context, drinksMenuLocal)).start();
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        if (!directoryToBeDeleted.delete())
            Log.d(LocalDrinksMenuManager.class.getSimpleName(),
                "Tried to delete local save, but it is non-existent already: "
                        + directoryToBeDeleted.getPath());
    }
}
