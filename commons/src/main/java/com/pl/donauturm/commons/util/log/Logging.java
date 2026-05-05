package com.pl.donauturm.commons.util.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs are loaded statically, once context is provided.
 * Logs are saved statically, as soon as any logging has happened.
 *
 * @param <S>
 */
public class Logging<S> {

    public static final String TAG = "com.pl.donauturm.Logging";
    public static final String KEY = "logs";
    private static final int LOG_CACHE_SIZE = 40;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static final LogList logs = new LogList(LOG_CACHE_SIZE);
    private final String tag;
    private final List<LoggerFilter<S>> loggerFilters = new ArrayList<>();
    private static final List<OnLogUpdate> logUpdateListeners = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static final Logging<String> DEFAULT_LOGGER = new Logging<>(Logging.class.getSimpleName());

    protected Logging(String tag) {
        this.tag = tag;
    }

    @AnyThread
    public static void post(String message){
        new Handler(Looper.getMainLooper()).post(() -> DEFAULT_LOGGER.iLog(message));
    }

    @AnyThread
    protected void log(S state) {
        new Handler(Looper.getMainLooper()).post(() -> iLog(state));
    }

    @MainThread
    protected void iLog(S state){
        for (LoggerFilter<S> loggerFilter : loggerFilters) {
            if (!loggerFilter.shouldLog(state))
                return;
        }
        String log = format(state);

        SimpleDateFormat simpleDateFormat = getLogFormat();
        String info = simpleDateFormat.format(Calendar.getInstance().getTime()) + " ";

        // push log to default android debug logger for debugging:
        Log.d(tag, log);

        // push log to java logger for intercepting if any class wants this data:
        getLogger().log(Level.INFO, log);

        // add log to limited list for persisting
        logs.add(info + log);
        for (OnLogUpdate listener : logUpdateListeners)
            listener.onLogUpdate(getLogs());

        // persist logs:
        saveLogs();
    }

    @NonNull
    @Contract(" -> new")
    public static SimpleDateFormat getLogFormat(){
        return new SimpleDateFormat("dd.MM. kk:mm:ss.SSS", Locale.getDefault());
    }

    protected String format(S state) {
        if (state == null)
            return "null";
        return state.toString();
    }

    public static void addListener(OnLogUpdate callback){
        logUpdateListeners.add(callback);
    }

    public static void removeListener(OnLogUpdate callback){
        logUpdateListeners.remove(callback);
    }

    public void addFilter(LoggerFilter<S> sLoggerFilter) {
        loggerFilters.add(sLoggerFilter);
    }

    public void removeFilter(LoggerFilter<S> sLoggerFilter) {
        loggerFilters.remove(sLoggerFilter);
    }

    @NonNull
    public static Logger getLogger() {
        return Logger.getLogger("dtspotify");
    }

    @NonNull
    public static List<String> getLogs() {
        if (logs.isSorting())
            return new ArrayList<>();
        return Collections.unmodifiableList(logs);
    }

    public static void clearLogs(){
        // clear logs
        logs.clear();
        for (OnLogUpdate listener : logUpdateListeners)
            listener.onLogUpdate(getLogs());

        // delete persisted logs:
        saveLogs();
    }

    public static void provideContext(Context context) {
        boolean initial = Logging.context == null;
        Logging.context = context;
        if (initial)
            loadLogs();
    }

    private static void loadLogs() {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        if (logs.isEmpty())
            logs.addAllOld(sp.getStringSet(KEY, Set.of()));
    }

    private static void saveLogs() {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(KEY, new HashSet<>(logs));
        editor.apply();
    }

    public interface LoggerFilter<S> {

        boolean shouldLog(S state);

    }

    public interface OnLogUpdate {

        @AnyThread
        void onLogUpdate(List<String> logs);

    }

}


