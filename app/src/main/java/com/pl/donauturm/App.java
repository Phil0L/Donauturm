package com.pl.donauturm;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import lv.chi.photopicker.ChiliPhotoPicker;
import lv.chi.photopicker.loader.ImageLoader;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ChiliPhotoPicker.INSTANCE.init(new GlideImageLoader(), "com.pl.donauturm.fileprovider");
    }

    private static class GlideImageLoader implements ImageLoader {

        public void loadImage(@NonNull Context context, @NonNull ImageView view, @NonNull Uri uri) {
            Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .centerCrop()
                    .into(view);
        }
    }
}


