package com.pl.donauturm.spotify.customrequests;

import android.graphics.Bitmap;
import android.util.Pair;

public class BitmapDownloadRequestWithContext<CON> extends BitmapDownloadRequest{

    private final CON context;

    public BitmapDownloadRequestWithContext(String url, CON context) {
        super(url);
        this.context = context;
    }

    public Pair<Bitmap, CON> requestContext() {
        return new Pair<>(super.request(), context);
    }

    public void requestAsyncContext(ResponseContextCallback<Bitmap, CON> callback) {
        super.requestAsync(bitmap -> {
            if (callback != null)
                callback.onResponse(bitmap, context);
        });
    }

    public interface ResponseContextCallback<S, CON>{
        void onResponse(S response, CON context);
    }

}
