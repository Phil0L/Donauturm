package com.pl.donauturm.dtspotify.commons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {

    private ScheduledExecutorService sceduler;
    private final long expiryMillis;
    private long startTime;
    private long expiryTime;
    @Nullable
    private OnTimer callback;
    private boolean running;
    private boolean rang;

    private Timer(long expiryMills) {
        expiryMillis = expiryMills;
    }

    public void start() {
        running = true;
        rang = false;
        expiryTime = System.currentTimeMillis() + expiryMillis;
        startTime = System.currentTimeMillis();
        sceduler = Executors.newSingleThreadScheduledExecutor();
        sceduler.scheduleWithFixedDelay(this::execute, expiryMillis, expiryMillis / 100, TimeUnit.MILLISECONDS);
    }

    public Timer autostart() {
        running = true;
        rang = false;
        expiryTime = System.currentTimeMillis() + expiryMillis;
        startTime = System.currentTimeMillis();
        sceduler = Executors.newSingleThreadScheduledExecutor();
        sceduler.scheduleWithFixedDelay(this::execute, expiryMillis, expiryMillis / 100, TimeUnit.MILLISECONDS);
        return this;
    }

    public void restart() {
        running = true;
        rang = false;
        expiryTime = System.currentTimeMillis() + expiryMillis;
        startTime = System.currentTimeMillis();
        sceduler.shutdown();
        sceduler = Executors.newSingleThreadScheduledExecutor();
        sceduler.scheduleWithFixedDelay(this::execute, expiryMillis, expiryMillis / 100, TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        running = false;
        rang = false;
        sceduler.shutdown();
    }

    private void execute(){
        if (!isExpired()) return;
        this.rang = true;
        if (callback != null) callback.onTimer();
        this.sceduler.shutdown();
    }

    public void onRing(OnTimer cb) {
        callback = cb;
        if (callback != null && isExpired()) callback.onTimer();
    }

    public boolean isExpired() {
        return rang || (System.currentTimeMillis() > expiryTime && running) ;
    }

    /**
     * @return progress as float between 0f anf 1f
     */
    public float getProgress() {
        if (!running) return 0f;
        if (isExpired()) return 1f;
        return (float) (System.currentTimeMillis() - startTime) / (expiryTime - startTime);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static Timer.Builder set(int duration) {
        return new Builder(duration);
    }

    public interface OnTimer {
        void onTimer();
    }

    public static class Builder {
        private final int value;

        private Builder(int value) {
            this.value = value;
        }

        public Timer millis() {
            return new Timer(value);
        }

        public Timer seconds() {
            return new Timer(value * 1000L);
        }

        public Timer minutes() {
            return new Timer(value * 1000L * 60L);
        }
    }


}
