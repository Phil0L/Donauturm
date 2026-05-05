package com.pl.donauturm.spotify.customrequests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class BitmapDownloadRequest extends Request<Void, Bitmap> {

    private final String url;
    private final static Map<String, Bitmap> cache = new LinkedHashMap<>(20, 0.75f, true){
        @Override
        protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
            return size() > 20;
        }
    };

    public BitmapDownloadRequest(String url) {
        super(null);
        this.url = url;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.GET;
    }

    @Override
    protected String requestPath() {
        return url;
    }

    @Override
    public String uri() {
        return url;
    }

    @Override
    protected Class<? extends Bitmap> provideResultClass(String body) {
        return null;
    }

    @Override
    public RequestBody bodyPublisher() {
        return null;
    }

    @Override
    public Bitmap request() {
        if (cache.containsKey(url)) return cache.get(url);
        return super.request();
    }

    @Override
    public void requestAsync(ResponseCallback<Bitmap> callback) {
        if (cache.containsKey(url)) {
            callback.onResponse(cache.get(url));
            return;
        }
        super.requestAsync(callback);
    }

    @Override
    protected Bitmap parseResult(ResponseBody responseBody) {
        try {
            InputStream inputStream = responseBody.byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            logIncoming(url);
            cacheBitmap(bitmap);
            return bitmap;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private void cacheBitmap(Bitmap bitmap) {
        cache.put(url, bitmap);
    }

    protected void logIncoming(String text) {
        System.out.println("<- " + text);
    }

}
