package com.pl.donauturm.dtspotify.commons.lyrics;

import androidx.annotation.NonNull;

import com.pl.donauturm.commons.util.lists.CallbackList;
import com.pl.donauturm.commons.util.log.Logging;

import java.util.Objects;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class LyricsManager {

    private Track track;
    private long time;
    private Lyrics lyrics;
    @NonNull
    private String pastLyrics = "";
    @NonNull
    private String futureLyrics = "";
    private final LyricsLogger logger;
    private final CallbackList<OnLyricsChange> callbackList;

    public LyricsManager() {
        this.logger = new LyricsLogger(getClass().getSimpleName());
        this.callbackList = new CallbackList<>();
        this.callbackList.add(logger);
    }

    public void updateTrack(Track track) {
        if (track == null) return;
        if (this.track == null || !this.track.getId().equals(track.getId()))
            updateTrackForce(track);
    }

    private void updateTrackForce(@NonNull Track track) {
        if (this.track != null && this.track.getId().equals(track.getId()))
            return;
        Lyrics.getAsync(track, lyrics -> {
            this.track = track;
            this.time = 0;
            this.lyrics = lyrics;
            this.checkForLyricsChange();
            this.lyricsTrackChanged();
        });
    }

    public void updateTime(long time) {
        this.time = time;
        this.checkForLyricsChange();
        this.lyricsTimeChanged();
    }

    private void checkForLyricsChange() {
        final String textPast = lyrics == null ? "" : lyrics.getTextPast(time);
        final String textFuture = lyrics == null ? "" : lyrics.getTextFuture(time);
        boolean change = !Objects.equals(textPast, this.pastLyrics) || !Objects.equals(textFuture, this.futureLyrics);
        this.pastLyrics = textPast;
        this.futureLyrics = textFuture;
        if (change)
            lyricsChanged();
    }

    public Lyrics getCurrentLyrics(){
        return lyrics;
    }

    public Track getCurrentTrack(){
        return track;
    }


    private void lyricsChanged() {
        this.callbackList.fire(c -> c.onLyricsChange(lyrics, track, pastLyrics, futureLyrics));
    }

    private void lyricsTimeChanged() {
        this.callbackList.fire(c -> c.onLyricsTimeChange(lyrics, track, time));
    }

    private void lyricsTrackChanged() {
        this.callbackList.fire(c -> c.onLyricsTrackChange(lyrics, track, pastLyrics, futureLyrics));
    }

    public boolean hasLyrics(){
        return lyrics != null && !lyrics.isError() && !lyrics.lines().isEmpty();
    }

    public void onLyricChange(OnLyricsChange callback){
        callbackList.add(callback);
    }

    public interface OnLyricsChange{
        void onLyricsTrackChange(Lyrics lyrics, Track track, String pastLyrics, String futureLyrics);
        void onLyricsChange(Lyrics lyrics, Track track, String pastLyrics, String futureLyrics);
        void onLyricsTimeChange(Lyrics lyrics, Track track, long time);
    }

    public static class LyricsLogger extends Logging<Lyrics> implements OnLyricsChange {

        protected LyricsLogger(String tag) {
            super(tag);
        }

        @Override
        public void onLyricsTrackChange(Lyrics lyrics, Track track, String pastLyrics, String futureLyrics) {
            if (lyrics != null)
                log(lyrics);
        }

        @Override
        public void onLyricsChange(Lyrics lyrics, Track track, String pastLyrics, String futureLyrics) {

        }

        @Override
        public void onLyricsTimeChange(Lyrics lyrics, Track track, long time) {

        }
    }
}
