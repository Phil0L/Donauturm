package com.pl.donauturm.dtspotify.commons.lyrics;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;
import com.pl.donauturm.spotify.customrequests.Request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class Lyrics {

    private final Track track;
    private final boolean error;
    private final SyncType syncType;
    private final List<Line> lines;

    @Nullable
    public static Lyrics get(Track track) {
        LyricsProvider.LyricsResult lr = new LyricsProvider(track).request();
        if (lr == null) return null;
        return lr.convert(track);
    }

    public static void getAsync(Track track, LyricsCallback callback) {
        new LyricsProvider(track).requestAsync(lr -> {
            if (lr == null) return;
            callback.onLyricsLoaded(lr.convert(track));
        });
    }

    public Lyrics(Track track, boolean error, SyncType syncType, List<Line> lines) {
        this.track = track;
        this.error = error;
        this.syncType = syncType;
        this.lines = lines;
    }

    public boolean isError() {
        return error || lines == null || lines.isEmpty();
    }

    public boolean isSynced() {
        return syncType == SyncType.LINE_SYNCED;
    }

    public SyncType getSyncType() {
        return syncType;
    }

    public List<Line> lines() {
        return lines;
    }

    public String getTextPast(long time) {
        return lines().stream().filter(line -> line.getTimeMillis() <= time).map(Line::getWords).collect(Collectors.joining("\n"));
    }

    public String getTextFuture(long time) {
        return lines().stream().filter(line -> line.getTimeMillis() > time).map(Line::getWords).collect(Collectors.joining("\n"));
    }

    public void getReplacementImage(Request.ResponseCallback<Bitmap> callback) {
        if (track != null)
            new BitmapDownloadRequest(track.getAlbum().getImages()[0].getUrl()).requestAsync(callback);
    }

    @NonNull
    @Override
    public String toString() {
        return "Lyrics{" +
                "track=" + track +
                ", error=" + error +
                ", syncType=" + syncType +
                ", lines=" + lines +
                '}';
    }

    public enum SyncType {
        LINE_SYNCED,
        UNSYNCED
    }

    public static final class Line {
        private static final Pattern timePattern = Pattern.compile("^(\\d+):(\\d+)\\.(\\d+)");
        private final String timeTag;
        private final String words;

        public Line(String timeTag, String words) {
            this.timeTag = timeTag;
            this.words = words;
        }

        public String getTimeTag() {
            return timeTag;
        }

        public long getTimeMillis() {
            Matcher matcher = timePattern.matcher(timeTag);
            if (matcher.find()) {
                int min = Integer.parseInt(matcher.group(1));
                int sec = Integer.parseInt(matcher.group(2));
                int mil = Integer.parseInt(matcher.group(3));
                return min * 60L * 1000L + sec * 1000L + mil * 10L;
            }
            return 0;
        }

        public String getWords() {
            if (words == null || words.isEmpty())
                return "...";
            return words;
        }
    }

    public interface LyricsCallback {
        void onLyricsLoaded(Lyrics lyrics);
    }

}
