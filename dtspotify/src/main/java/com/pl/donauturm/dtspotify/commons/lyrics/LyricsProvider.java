package com.pl.donauturm.dtspotify.commons.lyrics;

import androidx.annotation.NonNull;

import com.pl.donauturm.spotify.customrequests.Request;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class LyricsProvider extends Request<Void, LyricsProvider.LyricsResult> {

    private final String url;

    public LyricsProvider(@NonNull URI url) {
        super(null);
        this.url = url.toString();
    }

    public LyricsProvider(@NonNull Track track){
        super(null);
        this.url = track.getUri();
    }

    public LyricsProvider(String id){
        super(null);
        this.url = "https://open.spotify.com/track/" + id;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.GET;
    }

    @Override
    protected String requestPath() {
        return "https://spotify-lyrics-api-cyan.vercel.app/?url=" + url + "&format=lrc";
    }

    @Override
    public String uri() {
        return "https://spotify-lyrics-api-cyan.vercel.app/?url=" + url + "&format=lrc";
    }

    @Override
    protected Class<? extends LyricsResult> provideResultClass(String body) {
        return LyricsResult.class;
    }

    public static class LyricsResult {

       public boolean error;
       public LyricsSyncType syncType;
       public List<LyricLine> lines;

        public LyricsResult(boolean error, LyricsSyncType syncType, List<LyricLine> lines) {
            this.error = error;
            this.syncType = syncType;
            this.lines = lines;
        }

        public LyricsResult() {
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public LyricsSyncType getSyncType() {
            return syncType;
        }

        public void setSyncType(LyricsSyncType syncType) {
            this.syncType = syncType;
        }

        public List<LyricLine> getLines() {
            return lines;
        }

        public void setLines(List<LyricLine> lines) {
            this.lines = lines;
        }

        public Lyrics convert(Track track){
            return new Lyrics(
                    track,
                    error,
                    syncType == LyricsSyncType.LINE_SYNCED ? Lyrics.SyncType.LINE_SYNCED : syncType == LyricsSyncType.UNSYNCED ? Lyrics.SyncType.UNSYNCED : null,
                    lines.stream().map(line -> new Lyrics.Line(line.timeTag, line.words)).collect(Collectors.toList()));
        }
    }

    public enum LyricsSyncType {
        LINE_SYNCED,
        UNSYNCED
    }

    public static class LyricLine {
        public String timeTag;
        public String words;

        public LyricLine(String timeTag, String words) {
            this.timeTag = timeTag;
            this.words = words;
        }

        public LyricLine() {
        }

        public String getTimeTag() {
            return timeTag;
        }

        public void setTimeTag(String timeTag) {
            this.timeTag = timeTag;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
}