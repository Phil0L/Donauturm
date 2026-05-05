package com.pl.donauturm.controller.links;

import androidx.annotation.NonNull;

import com.pl.donauturm.R;

public class CamerasLink extends Link{

    public CamerasLink() {
    }

    @Override
    public int getNavItemId() {
        return R.id.nav_cameras;
    }

    @NonNull
    @Override
    public String getAppPackageName() {
        return "com.avigilon.acc_mobile";
    }
}
