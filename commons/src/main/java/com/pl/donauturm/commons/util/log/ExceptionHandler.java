package com.pl.donauturm.commons.util.log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;

import androidx.annotation.NonNull;

import com.pl.donauturm.commons.util.lists.CallbackList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "com.pl.donauturm.Exception";
    public static final String KEY_UE = "exception";
    public static final String KEY_SE = "system";
    private final Activity activity;
    private final CallbackList<OnUncaughtException> uncaughtExceptionCallbacks = new CallbackList<>();
    private final CallbackList<OnSystemError> systemErrorCallbacks = new CallbackList<>();
    private Thread.UncaughtExceptionHandler defaultUEH;
    private PrintStream defaultSE;
    private boolean interceptorActive;

    public ExceptionHandler(@NonNull Activity activity) {
        this.activity = activity;
        if (interceptorActive) return;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.defaultSE = System.err;
        System.setErr(new PrintStream(new ErrorStreamInterceptor(this::systemError)));
        this.interceptorActive = true;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable e) {
        logUE(thread, activity, e);
        uncaughtExceptionCallbacks.fire(c -> c.onUncaughtException(thread, activity, e));
        if (defaultUEH != null) defaultUEH.uncaughtException(thread, e);

    }

    public void systemError(@NonNull Thread thread, @NonNull String s){
        logSE(thread, activity, s);
        systemErrorCallbacks.fire(c -> c.onSystemError(thread, activity, s));
        if (defaultSE != null) {
            defaultSE.println(s);
            defaultSE.flush();
        }
    }

    @SuppressLint("ApplySharedPref")
    private void logUE(@NonNull Thread thread, @NonNull Activity activity, @NonNull Throwable e) {
        String data = uncaughtExceptionToString(thread, activity, e);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UE, data);
        editor.commit();
    }

    @SuppressLint("ApplySharedPref")
    private void logSE(@NonNull Thread thread, @NonNull Activity activity, @NonNull String s) {
        String data = systemErrorToString(thread, activity, s);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SE, data);
        editor.commit();
    }

    public String getLogUE(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_UE, null);
    }

    public boolean hasLogUE(){
        return getLogUE() != null;
    }

    public void resetLogUE(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_UE);
        editor.apply();
    }

    public void addUECallback(OnUncaughtException callback){
        uncaughtExceptionCallbacks.add(callback);
    }

    public String getLogSE(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SE, null);
    }

    public boolean hasLogSE(){
        return getLogUE() != null;
    }

    public void resetLogSE(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SE);
        editor.apply();
    }

    public void addSECallback(OnSystemError callback){
        systemErrorCallbacks.add(callback);
    }

    @NonNull
    public static String uncaughtExceptionToString(@NonNull Thread thread, @NonNull Activity activity, @NonNull Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("FATAL EXCEPTION: " + thread.getName());
        pw.println("Process: " + activity.getPackageName() + ", PID: " + Process.myPid());
        e.printStackTrace(pw);
        return sw.toString();
    }

    @NonNull
    public static String systemErrorToString(@NonNull Thread thread, @NonNull Activity activity, @NonNull String s){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("FATAL EXCEPTION: " + thread.getName());
        pw.println("Process: " + activity.getPackageName() + ", PID: " + Process.myPid());
        pw.println(s);
        return sw.toString();
    }

    public interface OnUncaughtException{
        void onUncaughtException(@NonNull Thread thread, @NonNull Activity activity, @NonNull Throwable e);
    }

    public interface OnSystemError{
        void onSystemError(@NonNull Thread thread, @NonNull Activity activity, @NonNull String s);
    }

    public static class ErrorStreamInterceptor extends OutputStream{

        private final StringBuilder string = new StringBuilder();
        private final InterceptorCallback callback;

        public ErrorStreamInterceptor(InterceptorCallback callback) {
            this.callback = callback;
        }

        @Override
        public void write(int b) {
            string.append((char) b);
        }

        @Override
        public void flush() throws IOException {
            callback.onFlush(Thread.currentThread(), toString());
            super.flush();
            string.setLength(0);
        }

        @NonNull
        @Override
        public String toString() {
            return string.toString();
        }

        public interface InterceptorCallback{
            void onFlush(@NonNull Thread thread, @NonNull String s);
        }
    }
}
