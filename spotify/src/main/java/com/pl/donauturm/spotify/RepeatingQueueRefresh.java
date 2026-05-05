package com.pl.donauturm.spotify;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.pl.donauturm.commons.util.log.Logging;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.special.PlaybackQueue;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class RepeatingQueueRefresh<T> {

    public int FREQUENCY = 500;
    public TimeUnit FREQUENCY_UNIT = TimeUnit.MILLISECONDS;
    public int START_DELAY = 0;
    public TimeUnit START_DELAY_UNIT = TimeUnit.SECONDS;
    public int ERROR_THRESHOLD = 3; // ERRORS REQUIRED FOR REPEATING ERROR

    private ScheduledExecutorService scheduler;
    private final QueueState lastState = new QueueState();
    private final T id;
    private final CallbackList<QueueUpdate<T>> callbacks;
    private final QueueLogger logger;
    private int errorCount;
    private SpotifyApi api;

    public RepeatingQueueRefresh(T id, QueueUpdate<T> callback) {
        this.logger = new QueueLogger(getClass().getSimpleName());
        this.id = id;
        this.callbacks = new CallbackList<>(callback);
        this.callbacks.add(logger);
        this.errorCount = 0;
    }

    public void startRefresh(SpotifyApi api) {
        this.api = api;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tryRefresh, START_DELAY_UNIT.toMillis(START_DELAY), FREQUENCY_UNIT.toMillis(FREQUENCY), TimeUnit.MILLISECONDS);
    }

    public void cancel(){
        scheduler.shutdown();
    }

    public void addCallback(@NonNull QueueUpdate<T> callback) {
        this.callbacks.add(callback);
        if (lastState.hasAnyNonNull())
            callback.update(lastState, lastState, id);
    }

    public void addCallbackWithoutCall(@NonNull QueueUpdate<T> callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(QueueUpdate<T> callback) {
        this.callbacks.remove(callback);
    }

    public void setFrequency(int frequency, TimeUnit unit){
        if (api != null) throw new IllegalStateException("You cannot set the frequency after Refresher started!");
        FREQUENCY = frequency;
        FREQUENCY_UNIT = unit;
    }

    public void setStartDelay(int delay, TimeUnit unit){
        if (api != null) throw new IllegalStateException("You cannot set the start delay after Refresher started!");
        START_DELAY = delay;
        START_DELAY_UNIT = unit;
    }

    public QueueState getLastState() {
        return lastState;
    }

    public void tryRefresh() {
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
            scheduler.shutdown();
        }
    }

    public void refresh() {
        api.getTheUsersQueue().build().executeAsync().whenComplete(((queue, throwable) -> {
            checkRepeatingError(queue, throwable);
            if (checkError(queue, throwable)) return;
            handleRefreshData(queue);
        }));
    }

    private void handleRefreshData(PlaybackQueue queue) {
        QueueState stateOfUpdates = lastState.set(
                /* QUEUE       */ queue.getQueue());
        callbacks.fire(c -> c.update(stateOfUpdates, lastState, id));
    }

    private boolean isError(Object object, Throwable throwable) {
        return object == null || throwable != null;
    }

    private boolean checkError(Object object, Throwable throwable) {
        if (isError(object, throwable)) {
            throwable.printStackTrace();
            callbacks.fire(c -> c.onQueueUpdateError(throwable, id));
            return true;
        }
        return false;
    }

    private void checkRepeatingError(Object object, Throwable throwable) {
        if (isError(object, throwable)) {
            errorCount++;
            if (errorCount >= ERROR_THRESHOLD) {
                callbacks.fire(c -> c.onQueueUpdateRepeatingError(errorCount, throwable, id));
            }
        } else {
            errorCount = 0;
        }
    }

    public interface QueueUpdate<T> {
        default void update(QueueState stateOfUpdates, QueueState currentState, T id) {
            if (currentState == null || stateOfUpdates == null) return;
            update(stateOfUpdates.tracks, currentState, id);
        }

        default void update(List<Track> updatesQueue, QueueState current, T id) {
            if (updatesQueue != null) {
                onQueueUpdate(current.tracks, current.ids, id);
                onQueueUpdate(current, id);
            }
        }

        @WorkerThread
        void onQueueUpdate(List<Track> queue, List<String> queueIds, T id);

        @WorkerThread
        void onQueueUpdate(QueueState state, T id);

        @WorkerThread
        void onQueueUpdateError(Throwable throwable, T id);

        @WorkerThread
        void onQueueUpdateRepeatingError(int errorCount, Throwable throwable, T id);
    }

    public interface DefaultQueueUpdate<T> extends QueueUpdate<T> {
        @Override
        @WorkerThread
        default void onQueueUpdate(List<Track> queue, List<String> queueIds, T id) {
        }

        @Override
        default void onQueueUpdate(QueueState state, T id){
        }

        @Override
        @WorkerThread
        default void onQueueUpdateError(Throwable throwable, T id) {
        }

        @Override
        @WorkerThread
        default void onQueueUpdateRepeatingError(int errorCount, Throwable throwable, T id) {
        }
    }

    public static class QueueUpdater<T> implements QueueUpdate<T> {

        @Override
        public final void update(QueueState stateOfUpdates, QueueState currentState, T id) {
            QueueUpdate.super.update(stateOfUpdates, currentState, id);
        }

        @Override
        public final void update(List<Track> queue, QueueState current, T id) {
            QueueUpdate.super.update(queue, current, id);
        }

        @Override
        @WorkerThread
        public void onQueueUpdate(List<Track> queue, List<String> queueIds, T id) {

        }

        @Override
        @WorkerThread
        public void onQueueUpdate(QueueState state, T id) {

        }

        @Override
        @WorkerThread
        public void onQueueUpdateError(Throwable throwable, T id) {

        }

        @Override
        @WorkerThread
        public void onQueueUpdateRepeatingError(int errorCount, Throwable throwable, T id) {

        }
    }

    public static class QueueState {
        private List<Track> tracks = null;
        private List<String> ids = null;

        public QueueState set(List<Track> queue) {
            QueueState stateOfUpdates = new QueueState();
            if (setQueue(queue)) stateOfUpdates.setQueue(queue);
            return stateOfUpdates;
        }

        public List<Track> getQueue() {
            return tracks;
        }

        public List<String> getQueueIds(){
            return ids;
        }

        public boolean setQueue(List<Track> queue) {
            if (queue == null) return false;
            if (this.tracks == null) {
                this.tracks = queue;
                this.ids = queue.stream().map(Track::getId).collect(Collectors.toList());
                return true;
            }
            List<String> newIds = queue.stream().map(Track::getId).collect(Collectors.toList());
            boolean r = !this.ids.equals(newIds);
            this.tracks = queue;
            this.ids = newIds;
            return r;
        }

        public boolean hasAnyNonNull() {
            return tracks != null;
        }

        @NonNull
        @Override
        public String toString() {
            return "QueueState{" +
                    "tracks=" + tracks +
                    ", ids=" + ids +
                    '}';
        }
    }

    public class QueueLogger extends Logging<QueueState> implements DefaultQueueUpdate<T> {

        protected QueueLogger(String tag) {
            super(tag);
        }

        @Override
        public void update(List<Track> updatesQueue, QueueState current, T id) {
            if (updatesQueue != null)
                log(current);
        }
    }

}
