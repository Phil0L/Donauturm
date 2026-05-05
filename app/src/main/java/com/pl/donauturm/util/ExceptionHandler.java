package com.pl.donauturm.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "com.pl.donauturm.Exception";
    public static final String KEY = "exception";

    private final Activity activity;
    private final Thread.UncaughtExceptionHandler defaultUEH;

    public ExceptionHandler(@NonNull Activity activity) {
        this.activity = activity;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable e) {
        logCode(thread, activity, e);
        if (defaultUEH != null) defaultUEH.uncaughtException(thread, e);
    }

    @SuppressLint("ApplySharedPref")
    private void logCode(@NonNull Thread thread, @NonNull Activity activity, @NonNull Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("FATAL EXCEPTION: " + thread.getName());
        pw.println("Process: " + activity.getPackageName() + ", PID: " + Process.myPid());
        e.printStackTrace(pw);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, sw.toString());
        editor.commit();
    }

    public String getLog(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY, null);
    }

    public boolean hasLog(){
        return getLog() != null;
    }

    public void resetLog(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY);
        editor.apply();
    }
}
