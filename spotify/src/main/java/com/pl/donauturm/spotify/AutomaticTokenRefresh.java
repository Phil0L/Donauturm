package com.pl.donauturm.spotify;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

public class AutomaticTokenRefresh {

    private ScheduledExecutorService scheduler;
    private SpotifyApi api;

    public void startRefresh(SpotifyApi api, int expiresIn){
        this.api = api;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refresh, 0, expiresIn-30, TimeUnit.SECONDS);
    }

    public void refresh(){
        try {
            AuthorizationCodeCredentials credentials = api.authorizationCodeRefresh().build().execute();
            api.setAccessToken(credentials.getAccessToken());
            if (credentials.getRefreshToken() != null && !credentials.getRefreshToken().isEmpty())
                api.setRefreshToken(credentials.getRefreshToken());
        } catch (Exception e) {
            e.printStackTrace();
            scheduler.shutdown();
        }
    }

}
