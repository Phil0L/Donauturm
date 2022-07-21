package com.pl.donauturm.pisignageapi.util;

import com.pl.donauturm.pisignageapi.model.session.messages.LoginMessage;
import com.pl.donauturm.pisignageapi.requests.SessionRequest;

import okhttp3.OkHttpClient;

public class ConnectionManager {

    private static ConnectionManager INSTANCE;
    private OkHttpClient client;
    private String token;
    private static String username;
    private static String password;

    public ConnectionManager(String u, String p) {
        INSTANCE = this;
        username = u;
        password = p;
    }

    public static ConnectionManager single() {
        if (INSTANCE != null) return INSTANCE;
        throw new RuntimeException("ConnectionManager not initialized");
    }

    public OkHttpClient getClient() {
        if (client != null) return client;
        return client = new OkHttpClient();
    }

    public String getToken() {
        if (token != null) return token;
        new SessionRequest(new LoginMessage(username, password)).request();
        return token;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }

    public void setToken(String token) {
        this.token = token;
    }

}
