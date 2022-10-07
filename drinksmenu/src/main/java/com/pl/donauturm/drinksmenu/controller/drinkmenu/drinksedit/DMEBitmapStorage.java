package com.pl.donauturm.drinksmenu.controller.drinkmenu.drinksedit;

import android.graphics.Bitmap;

/**
 * Since there is no intention of a feature to have multiple editors open at once, this class implements a singleton pattern.
 * This class is used to provide the editor with the necessary data in form of Images.
 */
public class DMEBitmapStorage {

    private static DMEBitmapStorage instance;

    private DMEBitmapStorage() {
    }

    public static DMEBitmapStorage getInstance() {
        if (instance == null) instance = new DMEBitmapStorage();
        return instance;
    }

    private Bitmap background;

    public Bitmap getBackground() {
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public void clear() {
        background = null;
    }

}
