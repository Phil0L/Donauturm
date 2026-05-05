package com.pl.donauturm.spotify;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.pl.donauturm.commons.util.log.Logging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class RepeatingPlaybackRefresh<T> {

    public int FREQUENCY = 500;
    public TimeUnit FREQUENCY_UNIT = TimeUnit.MILLISECONDS;
    public int START_DELAY = 0;
    public TimeUnit START_DELAY_UNIT = TimeUnit.SECONDS;
    public int ERROR_THRESHOLD = 5; // ERRORS REQUIRED FOR REPEATING ERROR

    private ScheduledExecutorService scheduler;
    private final PlaybackState lastState = new PlaybackState();
    private final T id;
    private final CallbackList<PlaybackUpdate<T>> callbacks;
    private final PlaybackLogger logger;
    private int successCount;
    private int errorCount;
    private boolean errorLoop = false;
    private SpotifyApi api;

    public RepeatingPlaybackRefresh(T id, PlaybackUpdate<T> callback) {
        this.logger = new PlaybackLogger(getClass().getSimpleName());
        this.id = id;
        this.callbacks = new CallbackList<>(callback);
        this.callbacks.add(logger);
        this.errorCount = 0;
        this.successCount = 0;
    }

    public void startRefresh(SpotifyApi api) {
        this.api = api;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tryRefresh, START_DELAY_UNIT.toMillis(START_DELAY), FREQUENCY_UNIT.toMillis(FREQUENCY), TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        scheduler.shutdown();
    }

    public void addCallback(@NonNull PlaybackUpdate<T> callback) {
        this.callbacks.add(callback);
        if (lastState.hasAnyNonNull())
            callback.update(lastState, lastState, null, id);
    }

    public void addCallbackWithoutCall(@NonNull PlaybackUpdate<T> callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(PlaybackUpdate<T> callback) {
        this.callbacks.remove(callback);
    }

    public void setFrequency(int frequency, TimeUnit unit) {
        if (api != null)
            throw new IllegalStateException("You cannot set the frequency after Refresher started!");
        FREQUENCY = frequency;
        FREQUENCY_UNIT = unit;
    }

    public void setStartDelay(int delay, TimeUnit unit) {
        if (api != null)
            throw new IllegalStateException("You cannot set the start delay after Refresher started!");
        START_DELAY = delay;
        START_DELAY_UNIT = unit;
    }

    public PlaybackState getLastState() {
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


    /**
     * If first api call fails, second cannot be executed.
     * Therefore the method must terminate.
     * This should also be considered an error and counted for repeating errors.
     * <p>
     * If the current state has no active value, and an error occurs, the current state should be set inactive.
     * <p>
     */
    public void refresh() {
        api.getInformationAboutUsersCurrentPlayback().build().executeAsync().whenComplete(((playback, throwable) -> {
            checkRepeatingError(playback, throwable);
            if (checkError(playback, throwable)) return;
            api.getTrack(playback.getItem().getId()).build().executeAsync().whenComplete(((track, throwable1) -> {
                checkRepeatingError(track, throwable1);
                if (checkError(track, throwable1)) return;
                handleRefreshData(playback, track);
            }));
        }));
    }

    private void handleRefreshData(@NonNull CurrentlyPlayingContext playback, Track track) {
        PlaybackState stateOfUpdates = lastState.set(
                /* ACTIVE       */ track != null,
                /* IS PLAYING   */ playback.getIs_playing(),
                /* IS SHUFFLE   */ playback.getShuffle_state(),
                /* IS REPEAT    */ playback.getRepeat_state() != null ? !playback.getRepeat_state().toLowerCase().contains("off") : null,
                /* TRACK NAME   */ playback.getItem().getName(),
                /* ARTIST       */ track != null && track.getArtists() != null && track.getArtists().length > 0 ? track.getArtists()[0].getName() : "Name Error",
                /* ALBUM        */ track != null && track.getAlbum() != null ? track.getAlbum().getName() : "Single",
                /* IMAGE URL    */ track != null && track.getAlbum() != null && track.getAlbum().getImages() != null && track.getAlbum().getImages().length > 0 ? track.getAlbum().getImages()[0].getUrl() : "",
                /* PROGRESS     */ playback.getProgress_ms(),
                /* DURATION     */ playback.getItem().getDurationMs(),
                /* VOLUME       */ playback.getDevice().getVolume_percent());
        callbacks.fire(c -> c.update(stateOfUpdates, lastState, track, id));
    }

    private boolean isError(Object object, Throwable throwable) {
        return object == null || throwable != null;
    }

    private boolean isFailedRequest(Object object, Throwable throwable) {
        return object == null && throwable == null;
    }

    private boolean checkError(Object object, Throwable throwable) {
        if (isFailedRequest(object, throwable) && lastState.isActive() == null) {
            PlaybackState stateOfUpdates = new PlaybackState();
            lastState.setActive(false);
            stateOfUpdates.setActive(false);
            callbacks.fire(c -> c.update(stateOfUpdates, lastState, null, id));
            return true;
        }
        if (isFailedRequest(object, throwable)) {
            return true;
        }
        if (isError(object, throwable)) {
            deactivate();
            throwable.printStackTrace();
            callbacks.fire(c -> c.onPlaybackUpdateError(throwable, id));
            return true;
        }
        return false;
    }

    private void checkRepeatingError(Object object, Throwable throwable) {
        if (isError(object, throwable)) {
            errorCount++;
            successCount = 0;
            if (errorCount >= ERROR_THRESHOLD) {
                callbacks.fire(c -> c.onPlaybackUpdateRepeatingError(errorCount, throwable, id));
                errorLoop = true;
                errorCount = 0;
            }
        } else {
            successCount++;
            errorCount = 0;
            if (errorLoop) {// Recovered from repeating error
                errorLoop = false;
                ERROR_THRESHOLD <<= 1; // Double threshold
            }
            if (successCount >= 20) { // Stable connection
                ERROR_THRESHOLD = 5;
            }
        }
    }

    private void deactivate() {
        if (lastState.setActive(false))
            callbacks.fire(c -> c.onActiveUpdate(false, null, lastState, id));
    }

    public interface PlaybackUpdate<T> {
        default void update(PlaybackState stateOfUpdates, PlaybackState currentState, Track track, T id) {
            if (currentState == null || stateOfUpdates == null) return;
            update(stateOfUpdates.isActive(), stateOfUpdates.isPlaying(), stateOfUpdates.isShuffle(), stateOfUpdates.isRepeating(), stateOfUpdates.getTrackName(), stateOfUpdates.getArtistName(), stateOfUpdates.getAlbumName(), stateOfUpdates.getAlbumImageUrl(), stateOfUpdates.getTrackProgress(), stateOfUpdates.getTrackLength(), stateOfUpdates.getVolume(), track, currentState, id);
        }

        default void update(Boolean active, Boolean playing, Boolean shuffle, Boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, Integer trackProgress, Integer trackLength, Integer volume, Track track, PlaybackState current, T id) {
            if (active != null)
                onActiveUpdate(active, track, current, id);
            if (playing != null)
                onPlayingUpdate(playing, track, current, id);
            if (shuffle != null)
                onShuffleUpdate(shuffle, current, id);
            if (repeating != null)
                onRepeatingUpdate(repeating, current, id);
            if (trackName != null)
                onTrackNameUpdate(trackName, track, current, id);
            if (artistName != null)
                onArtistNameUpdate(artistName, current, id);
            if (albumName != null)
                onAlbumNameUpdate(albumName, current, id);
            if (albumImageUrl != null)
                onAlbumImageUrlUpdate(albumImageUrl, current, id);
            if (trackProgress != null)
                onTrackProgressUpdate(trackProgress, current, id);
            if (trackLength != null)
                onTrackLengthUpdate(trackLength, current, id);
            if (volume != null)
                onVolumeUpdate(volume, current, id);
            if (active != null || playing != null || shuffle != null || repeating != null || trackName != null || artistName != null || albumName != null || albumImageUrl != null || trackProgress != null || trackLength != null || volume != null) {
                onAnyPlaybackUpdate(current.isActive(false), current.isPlaying(false), current.isShuffle(false), current.isRepeating(false), current.getTrackName(), current.getArtistName(), current.getAlbumName(), current.getAlbumImageUrl(), current.getTrackProgress(0), current.getTrackLength(0), current.getVolume(0), id);
                onAnyPlaybackUpdate(current, id);
            }
        }

        @WorkerThread
        void onAnyPlaybackUpdate(boolean active, boolean playing, boolean shuffle, boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, int trackProgress, int trackLength, int volume, T id);

        @WorkerThread
        void onAnyPlaybackUpdate(PlaybackState state, T id);

        @WorkerThread
        void onActiveUpdate(boolean active, Track track, PlaybackState state, T id);

        @WorkerThread
        void onPlayingUpdate(boolean playing, Track track, PlaybackState state, T id);

        @WorkerThread
        void onShuffleUpdate(boolean shuffle, PlaybackState state, T id);

        @WorkerThread
        void onRepeatingUpdate(boolean repeating, PlaybackState state, T id);

        @WorkerThread
        void onTrackNameUpdate(String trackName, Track track, PlaybackState state, T id);

        @WorkerThread
        void onArtistNameUpdate(String artistName, PlaybackState state, T id);

        @WorkerThread
        void onAlbumNameUpdate(String albumName, PlaybackState state, T id);

        @WorkerThread
        void onAlbumImageUrlUpdate(String albumImageUrl, PlaybackState state, T id);

        @WorkerThread
        void onTrackProgressUpdate(int trackProgress, PlaybackState state, T id);

        @WorkerThread
        void onTrackLengthUpdate(int trackLength, PlaybackState state, T id);

        @WorkerThread
        void onVolumeUpdate(int volume, PlaybackState state, T id);

        @WorkerThread
        void onPlaybackUpdateError(Throwable throwable, T id);

        @WorkerThread
        void onPlaybackUpdateRepeatingError(int errorCount, Throwable throwable, T id);
    }

    public interface DefaultPlaybackUpdate<T> extends PlaybackUpdate<T> {
        @Override
        @WorkerThread
        default void onAnyPlaybackUpdate(boolean active, boolean playing, boolean shuffle, boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, int trackProgress, int trackLength, int volume, T id) {
        }

        @WorkerThread
        default void onAnyPlaybackUpdate(PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        default void onActiveUpdate(boolean active, Track track, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onPlayingUpdate(boolean playing, Track track, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onShuffleUpdate(boolean shuffle, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onRepeatingUpdate(boolean repeating, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onTrackNameUpdate(String trackName, Track track, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onArtistNameUpdate(String artistName, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onAlbumNameUpdate(String albumName, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onAlbumImageUrlUpdate(String albumImageUrl, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onTrackProgressUpdate(int trackProgress, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onTrackLengthUpdate(int trackLength, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onVolumeUpdate(int volume, PlaybackState state, T id) {
        }

        @Override
        @WorkerThread
        default void onPlaybackUpdateError(Throwable throwable, T id) {
        }

        @Override
        @WorkerThread
        default void onPlaybackUpdateRepeatingError(int errorCount, Throwable throwable, T id) {
        }
    }

    public static class PlaybackUpdater<T> implements PlaybackUpdate<T> {

        @Override
        public final void update(PlaybackState stateOfUpdates, PlaybackState currentState, Track track, T id) {
            PlaybackUpdate.super.update(stateOfUpdates, currentState, track, id);
        }

        @Override
        public final void update(Boolean active, Boolean playing, Boolean shuffle, Boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, Integer trackProgress, Integer trackLength, Integer volume, Track track, PlaybackState current, T id) {
            PlaybackUpdate.super.update(active, playing, shuffle, repeating, trackName, artistName, albumName, albumImageUrl, trackProgress, trackLength, volume, track, current, id);
        }

        @Override
        @WorkerThread
        public void onAnyPlaybackUpdate(boolean active, boolean playing, boolean shuffle, boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, int trackProgress, int trackLength, int volume, T id) {

        }

        @Override
        @WorkerThread
        public void onAnyPlaybackUpdate(PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onActiveUpdate(boolean active, Track track, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onPlayingUpdate(boolean playing, Track track, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onShuffleUpdate(boolean shuffle, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onRepeatingUpdate(boolean repeating, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onTrackNameUpdate(String trackName, Track track, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onArtistNameUpdate(String artistName, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onAlbumNameUpdate(String albumName, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onAlbumImageUrlUpdate(String albumImageUrl, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onTrackProgressUpdate(int trackProgress, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onTrackLengthUpdate(int trackLength, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onVolumeUpdate(int volume, PlaybackState state, T id) {

        }

        @Override
        @WorkerThread
        public void onPlaybackUpdateError(Throwable throwable, T id) {

        }

        @Override
        @WorkerThread
        public void onPlaybackUpdateRepeatingError(int errorCount, Throwable throwable, T id) {

        }
    }

    public static class PlaybackState {
        private Boolean active = null;
        private Boolean playing = null;
        private Boolean shuffle = null;
        private Boolean repeating = null;
        private String trackName = null;
        private String artistName = null;
        private String albumName = null;
        private String albumImageUrl = null;
        private Integer trackProgress = null;
        private Integer trackLength = null;
        private Integer volume = null;

        public PlaybackState set(Boolean active, Boolean playing, Boolean shuffle, Boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, Integer trackProgress, Integer trackLength, Integer volume) {
            PlaybackState stateOfUpdates = new PlaybackState();
            if (setActive(active)) stateOfUpdates.setActive(active);
            if (setPlaying(playing)) stateOfUpdates.setPlaying(playing);
            if (setShuffle(shuffle)) stateOfUpdates.setShuffle(shuffle);
            if (setRepeating(repeating)) stateOfUpdates.setRepeating(repeating);
            if (setTrackName(trackName)) stateOfUpdates.setTrackName(trackName);
            if (setArtistName(artistName)) stateOfUpdates.setArtistName(artistName);
            if (setAlbumName(albumName)) stateOfUpdates.setAlbumName(albumName);
            if (setAlbumImage(albumImageUrl)) stateOfUpdates.setAlbumImage(albumImageUrl);
            if (setTrackProgress(trackProgress)) stateOfUpdates.setTrackProgress(trackProgress);
            if (setTrackLength(trackLength)) stateOfUpdates.setTrackLength(trackLength);
            if (setVolume(volume)) stateOfUpdates.setVolume(volume);
            return stateOfUpdates;
        }

        public Boolean isActive() {
            return active;
        }

        public boolean isActive(boolean def) {
            if (active == null) return def;
            return active;
        }

        public boolean setActive(Boolean active) {
            if (active == null) return false;
            if (this.active == null) return (this.active = active) == active;
            boolean r = active != this.active;
            this.active = active;
            return r;
        }

        public Boolean isPlaying() {
            return playing;
        }

        public boolean isPlaying(boolean def) {
            if (playing == null) return def;
            return playing;
        }

        public boolean setPlaying(Boolean playing) {
            if (playing == null) return false;
            if (this.playing == null) return (this.playing = playing) == playing;
            boolean r = playing != this.playing;
            this.playing = playing;
            return r;
        }

        public Boolean isShuffle() {
            return shuffle;
        }

        public boolean isShuffle(boolean def) {
            if (shuffle == null) return def;
            return shuffle;
        }

        public boolean setShuffle(Boolean shuffle) {
            if (shuffle == null) return false;
            if (this.shuffle == null) return (this.shuffle = shuffle) == shuffle;
            boolean r = shuffle != this.shuffle;
            this.shuffle = shuffle;
            return r;
        }

        public Boolean isRepeating() {
            return repeating;
        }

        public boolean isRepeating(boolean def) {
            if (repeating == null) return def;
            return repeating;
        }

        public boolean setRepeating(Boolean repeating) {
            if (repeating == null) return false;
            if (this.repeating == null) return (this.repeating = repeating) == repeating;
            boolean r = repeating != this.repeating;
            this.repeating = repeating;
            return r;
        }

        public String getTrackName() {
            return trackName;
        }

        public boolean setTrackName(String trackName) {
            if (trackName == null) return false;
            if (this.trackName == null) {
                this.trackName = trackName;
                return true;
            }
            boolean r = !trackName.equals(this.trackName);
            this.trackName = trackName;
            return r;
        }

        public String getArtistName() {
            return artistName;
        }

        public boolean setArtistName(String artistName) {
            if (artistName == null) return false;
            if (this.artistName == null) {
                this.artistName = artistName;
                return true;
            }
            boolean r = !artistName.equals(this.artistName);
            this.artistName = artistName;
            return r;
        }

        public String getAlbumName() {
            return albumName;
        }

        public boolean setAlbumName(String albumName) {
            if (albumName == null) return false;
            if (this.albumName == null) {
                this.albumName = albumName;
                return true;
            }
            boolean r = !albumName.equals(this.albumName);
            this.albumName = albumName;
            return r;
        }

        public String getAlbumImageUrl() {
            return albumImageUrl;
        }

        public boolean setAlbumImage(String albumImageUrl) {
            if (albumImageUrl == null) return false;
            if (this.albumImageUrl == null) {
                this.albumImageUrl = albumImageUrl;
                return true;
            }
            boolean r = !albumImageUrl.equals(this.albumImageUrl);
            this.albumImageUrl = albumImageUrl;
            return r;
        }

        public Integer getTrackProgress() {
            return trackProgress;
        }

        public int getTrackProgress(int def) {
            if (trackProgress == null) return def;
            return trackProgress;
        }

        public float getTrackPercentile() {
            return ((float) getTrackProgress(0)) / getTrackLength(1);
        }

        public boolean setTrackProgress(Integer trackProgress) {
            if (trackProgress == null) return false;
            if (this.trackProgress == null) {
                this.trackProgress = trackProgress;
                return true;
            }
            boolean r = !trackProgress.equals(this.trackProgress);
            this.trackProgress = trackProgress;
            return r;
        }

        public Integer getTrackLength() {
            return trackLength;
        }

        public int getTrackLength(int def) {
            if (trackLength == null) return def;
            return trackLength;
        }

        public int getTrackLengthRemainder() {
            return getTrackLength(1) - getTrackProgress(0);
        }

        public boolean setTrackLength(Integer trackLength) {
            if (trackLength == null) return false;
            if (this.trackLength == null) {
                this.trackLength = trackLength;
                return true;
            }
            boolean r = !trackLength.equals(this.trackLength);
            this.trackLength = trackLength;
            return r;
        }

        public Integer getVolume() {
            return volume;
        }

        public int getVolume(int def) {
            if (volume == null) return def;
            return volume;
        }

        public boolean setVolume(Integer volume) {
            if (volume == null) return false;
            if (this.volume == null) {
                this.volume = volume;
                return true;
            }
            boolean r = !volume.equals(this.volume);
            this.volume = volume;
            return r;
        }

        public boolean hasAnyNonNull() {
            return active != null || playing != null || shuffle != null || repeating != null || trackName != null || artistName != null || albumName != null || albumImageUrl != null || trackProgress != null || trackLength != null || volume != null;
        }

        @NonNull
        @Override
        public String toString() {
            return "PlaybackState{" +
                    "active=" + active +
                    ", playing=" + playing +
                    ", shuffle=" + shuffle +
                    ", repeating=" + repeating +
                    ", trackName='" + trackName + '\'' +
                    ", artistName='" + artistName + '\'' +
                    ", albumName='" + albumName + '\'' +
                    ", albumImageUrl='" + albumImageUrl + '\'' +
                    ", trackProgress=" + trackProgress +
                    ", trackLength=" + trackLength +
                    ", volume=" + volume +
                    '}';
        }
    }

    public class PlaybackLogger extends Logging<PlaybackState> implements DefaultPlaybackUpdate<T> {

        protected PlaybackLogger(String tag) {
            super(tag);
        }

        @Override
        public void update(Boolean active, Boolean playing, Boolean shuffle, Boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, Integer trackProgress, Integer trackLength, Integer volume, Track track, PlaybackState current, T id) {
            if (active != null || playing != null || shuffle != null || repeating != null || trackName != null || artistName != null || albumName != null || albumImageUrl != null || volume != null) {
                log(current);
            }
        }
    }

}
