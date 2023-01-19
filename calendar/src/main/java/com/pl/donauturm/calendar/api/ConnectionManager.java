package com.pl.donauturm.calendar.api;

import okhttp3.OkHttpClient;

public class ConnectionManager {

    private static ConnectionManager INSTANCE;
    private OkHttpClient client;
    private String token;

    public ConnectionManager() {
        INSTANCE = this;
    }

    public static ConnectionManager single() {
        if (INSTANCE != null) return INSTANCE;
        throw new RuntimeException("ConnectionManager not initialized");
    }

    public OkHttpClient getClient() {
        if (client != null) return client;
        return client = new OkHttpClient();
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }

    public void setToken(String token) {
        this.token = token;
    }

}
