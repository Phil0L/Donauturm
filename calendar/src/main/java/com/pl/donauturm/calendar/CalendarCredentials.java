package com.pl.donauturm.calendar;

import android.content.Context;
import android.content.SharedPreferences;

public class CalendarCredentials {

    private static CalendarCredentials loadedCredentials;
    private static final String PREFS_NAME = "CalendarCredentials";
    private static final String PREFS_USERNAME = "username";
    private static final String PREFS_PASSWORD = "password";

    private String username;
    private String password;

    public CalendarCredentials() {
    }

    public CalendarCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static void deleteCredentials(Context context) {
        loadedCredentials = null;
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        settings.edit().clear().apply();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void save(Context context) {
        loadedCredentials = this;
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_USERNAME, username);
        editor.putString(PREFS_PASSWORD, password);
        editor.apply();
    }


    public static CalendarCredentials getCredentials(Context context) {
        if (loadedCredentials != null) {
            return loadedCredentials;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences == null) return null;
        String username = sharedPreferences.getString(PREFS_USERNAME, null);
        String password = sharedPreferences.getString(PREFS_PASSWORD, null);
        if (username == null || password == null) return null;
        loadedCredentials = new CalendarCredentials(username, password);
        return loadedCredentials;
    }

}
