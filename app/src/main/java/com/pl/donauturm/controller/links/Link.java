package com.pl.donauturm.controller.links;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

public abstract class Link {

    @IdRes
    public abstract int getNavItemId();

    @NonNull
    public abstract String getAppPackageName();

    public void open(Context context) {
        String appPackageName = getAppPackageName();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appPackageName));
        }  // We found the activity now start the activity

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
