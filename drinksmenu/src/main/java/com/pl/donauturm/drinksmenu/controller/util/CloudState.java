package com.pl.donauturm.drinksmenu.controller.util;

import com.pl.donauturm.drinksmenu.R;

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


    public interface OnCloudStateChangedListener {
        void onCloudStateChanged(CloudState state);
    }

}