package com.pl.donauturm.drinksmenu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pl.donauturm.pisignageapi.requests.Request;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class BitmapDownloadRequest extends Request<Void, Bitmap> {

    private final String url;

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
    public Headers headers() {
        Map<String, String> map = new HashMap<>();
        if (requiresToken()) {
            map.put("x-access-token", ConnectionManager.single().getToken());
        }
        return Headers.of(map);
    }

    @Override
    protected boolean requiresToken() {
        return true;
    }

    @Override
    protected Bitmap parseResult(ResponseBody responseBody) {
        InputStream inputStream = responseBody.byteStream();
        return BitmapFactory.decodeStream(inputStream);
    }
}
