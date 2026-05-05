package com.pl.donauturm.spotify;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequestWithContext;

public interface BitmapLoadable<S extends BitmapLoadable<S, D>, D> {
    String getBitmapUrl();
    void setBitmap(Bitmap bitmap);
    default void loadBitmap(BitmapLoaded<S, D> callback, S self, D data){
        if (getBitmapUrl() == null || getBitmapUrl().isEmpty()) return;
        Pair<S, D> queueTrackIntegerPair = new Pair<>(self, data);
        new BitmapDownloadRequestWithContext<>(getBitmapUrl(), queueTrackIntegerPair).requestAsyncContext((bitmap, pair) -> {
            S self1 = pair.first;
            D data1 = pair.second;
            new Handler(Looper.getMainLooper()).post(() -> {
                self1.setBitmap(bitmap);
                callback.onBitmapLoaded(bitmap, self1, data1);
            });
        });
    }

    interface BitmapLoaded<S extends BitmapLoadable<S, D>, D>{
        void onBitmapLoaded(Bitmap bitmap, S item, D data);
    }

}
