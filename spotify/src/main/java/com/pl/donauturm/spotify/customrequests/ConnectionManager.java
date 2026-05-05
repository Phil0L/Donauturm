package com.pl.donauturm.spotify.customrequests;

import okhttp3.OkHttpClient;

public class ConnectionManager {

    private static ConnectionManager INSTANCE;
    private OkHttpClient client;

    public ConnectionManager() {

        INSTANCE = this;
    }

    public static ConnectionManager single() {
        if (INSTANCE == null) INSTANCE = new ConnectionManager();
        return INSTANCE;
    }

    public OkHttpClient getClient() {
        if (client != null) return client;
        return client = new OkHttpClient();
    }

}
