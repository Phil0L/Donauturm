package com.pl.donauturm.drinksmenu.controller.drinks;

import android.content.Context;

import com.pl.donauturm.drinksmenu.model.DrinksMenu;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class LocalDrinksManager {

    private static final Set<String> LOCAL_DRINK_FILES = new HashSet<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadAllLocalSaves(Context context) {

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadAllLocalSavesAsync(Context context) {

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadLocalSave(Context context, String name) {


    }

    public static void deleteAllLocalSaves(Context context) {

    }

    public static void deleteLocalSave(Context context, String name) {

    }

    public static Set<String> getLoadedLocalDrinkNames() {
        return LOCAL_DRINK_FILES;
    }

    public static boolean containsLocalSave(Context context, String name) {
        return false;
    }

    public static void saveLocalSave(Context context, DrinksMenu drinksMenu) {

    }

    public static void saveLocalSaveAsync(Context context, DrinksMenu drinksMenu) {
        new Thread(() -> saveLocalSave(context, drinksMenu)).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDirectory(File directoryToBeDeleted) {

    }
}
