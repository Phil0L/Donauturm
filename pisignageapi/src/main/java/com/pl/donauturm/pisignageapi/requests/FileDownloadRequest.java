package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class FileDownloadRequest extends Request<Void, File> {

    private final File file;
    private final String url;

    public FileDownloadRequest(String url, File file) {
        super(null);
        this.url = url;
        this.file = file;
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
    protected Class<? extends File> provideResultClass(String body) {
        return null;
    }

    @Override
    public RequestBody bodyPublisher() {
        return null;
    }

    @Override
    public Headers headers() {
        Map<String, String> map = new HashMap<>();
//        map.put("accept", "application/json");
//        map.put("Content-type", "application/json");
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
    protected File parseResult(ResponseBody responseBody) {
        try {
            InputStream inputStream = responseBody.byteStream();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            logIncoming(file.getAbsolutePath());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return file;
    }

    protected void logIncoming(String body) {
        System.out.println("<- " + file.getAbsolutePath());
    }

}
