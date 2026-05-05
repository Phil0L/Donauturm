package com.pl.donauturm.controller.links;

import androidx.annotation.NonNull;

import com.pl.donauturm.R;

public class AlarmLink extends Link{

    public AlarmLink() {
    }

    @Override
    public int getNavItemId() {
        return R.id.nav_alarm;
    }

    @NonNull
    @Override
    public String getAppPackageName() {
        return "com.TelenotElectronic.BuildSec";
    }
}
