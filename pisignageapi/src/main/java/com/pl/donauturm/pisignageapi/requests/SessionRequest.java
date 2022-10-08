package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.session.messages.LoggedinMessage;
import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.util.ConnectionManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SessionRequest extends Request<LoginMessage, LoggedinMessage> {

    public static final int LOGIN_SUCCESS = 0;
    public static final int HTTP_ERROR = -1;
    public static final int NO_CONNECTION = -2;
    public static final int WRONG_CREDENTIALS = -3;

    public SessionRequest(LoginMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.POST;
    }

    @Override
    protected String requestPath() {
        return "session";
    }

    @Override
    protected Class<? extends LoggedinMessage> provideResultClass(String body) {
        return LoggedinMessage.class;
    }

    @Override
    protected boolean requiresToken() {
        return false;
    }

    @Override
    protected void onResponse(LoggedinMessage response) {
        ConnectionManager.single().setToken(response.getToken());
    }

    public Response requestResponse(CookieJar cookieJar) {
        logOutgoing();
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(uri())
                .method(method(), bodyPublisher())
                .headers(headers())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() < 200 || response.code() >= 300)
                throw new IOException("Unsupported status code: " + response.code());
            return response;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    public void testConnection(ResponseCallback<Integer> cb) {
        logOutgoing();
        OkHttpClient client = new OkHttpClient.Builder().build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(uri())
                .method(method(), bodyPublisher())
                .headers(headers())
                .build();
        new Thread(() -> client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.code() == 401) {
                    cb.onResponse(WRONG_CREDENTIALS);
                    return;
                }
                if (response.code() < 200 || response.code() >= 300) {
                    cb.onResponse(HTTP_ERROR);
                    return;
                }
                final ResponseBody body = response.body();
                if (body == null) {
                    cb.onResponse(HTTP_ERROR);
                    return;
                }
                LoggedinMessage re = parseResult(body);
                if (re == null) {
                    cb.onResponse(WRONG_CREDENTIALS);
                    return;
                }
                cb.onResponse(LOGIN_SUCCESS);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                cb.onResponse(NO_CONNECTION);
            }

        })).start();
    }
}
