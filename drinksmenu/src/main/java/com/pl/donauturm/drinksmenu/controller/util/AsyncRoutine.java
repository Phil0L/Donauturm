package com.pl.donauturm.drinksmenu.controller.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncRoutine<R> {

    private boolean running = false;
    private int taskIndex = -1;
    private AsyncRoutineCallback<R> callback;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @WorkerThread
    public abstract void startTask(int index, Object... args);

    @AnyThread
    protected void nextTask(Object... args) {
        taskIndex++;
        executor.execute(() -> startTask(taskIndex, args));
    }

    @AnyThread
    protected void finish(R result) {
        running = false;
        if (callback != null)
            handler.post(() -> callback.onResult(result));

    }

    @AnyThread
    protected void error() {
        running = false;
        if (callback instanceof AsyncRoutineResult) {
            handler.post(() -> ((AsyncRoutineResult<R>) callback).onError(null));
        }
    }

    @AnyThread
    protected void error(Exception e) {
        running = false;
        if (callback instanceof AsyncRoutineResult) {
            handler.post(() -> ((AsyncRoutineResult<R>) callback).onError(e));
        }
    }

    @AnyThread
    public void start() {
        if (!running) {
            taskIndex = -1;
            running = true;
            nextTask();
        }
    }

    @AnyThread
    public void start(AsyncRoutineCallback<R> callback) {
        if (!running) {
            taskIndex = -1;
            running = true;
            setCallback(callback);
            nextTask();
        }
    }

    @AnyThread
    public AsyncRoutine<R> setCallback(AsyncRoutineCallback<R> callback) {
        this.callback = callback;
        return this;
    }

    public abstract static class AsyncRoutineResult<Re> implements AsyncRoutineCallback<Re> {
        @MainThread
        public abstract void onResult(Re result);
        @MainThread
        public void onError(Exception e) {
        }
    }

    public interface AsyncRoutineCallback<R> {
        @MainThread
        void onResult(R result);
    }

}
