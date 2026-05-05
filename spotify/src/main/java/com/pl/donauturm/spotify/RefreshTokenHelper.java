package com.pl.donauturm.spotify;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenHelper {

    private static RefreshTokenHelper INSTANCE = null;

    private final Map<String, String> refreshTokens;

    public static RefreshTokenHelper getInstance(){
        if (INSTANCE != null) return INSTANCE;
        return new RefreshTokenHelper();
    }

    private RefreshTokenHelper() {
        refreshTokens = new HashMap<>();
        INSTANCE = this;
    }

    public void set(Context context, @NonNull String username, @NonNull String token){
        refreshTokens.put(username, token);
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.pl.donauturm.spotify", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(username, token).apply();
    }

    @Nullable
    public String get(Context context, @NonNull String username){
        if (refreshTokens.containsKey(username)) return refreshTokens.get(username);
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.pl.donauturm.spotify", Context.MODE_PRIVATE);
        return sharedPreferences.getString(username, null);
    }


}
