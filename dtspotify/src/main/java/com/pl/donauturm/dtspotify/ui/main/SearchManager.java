package com.pl.donauturm.dtspotify.ui.main;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.pl.donauturm.spotify.CallbackList;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class SearchManager {

    private final CallbackList<OnSearchResult> callbackList = new CallbackList<>();
    private final SpotifyApi api;
    private final Searcher searcher;
    private String searchInput;

    public SearchManager(SpotifyApi api) {
        this.api = api;
        this.searcher = new Searcher();
        this.searcher.start();
    }

    public void searchChanged(@NonNull String search) {
        this.searchInput = search;
        this.searcher.queueSearch(search);
    }

    public void cancel() {
        this.searcher.interrupt();
    }

    private void onSearchCompleted(@NonNull List<Track> results, @NonNull String search) {
        if (search.equals(searchInput))
            new Handler(Looper.getMainLooper()).post(() ->
                    callbackList.fire(c -> c.onSearchResult(results, search)));
    }

    public void onSearchCompleted(OnSearchResult callback){
        callbackList.add(callback);
    }

    public class Searcher extends Thread {

        @NonNull
        private String search = "";

        @NonNull
        private String nextSearch = "";

        public void queueSearch(String s) {
            if (search.isEmpty())
                search = s;
            else
                nextSearch = s;
        }

        @Override
        @SuppressWarnings("")
        public void run() {
            while (true) {
                try {
                    if (search.isEmpty())
                        Thread.sleep(100);
                    else
                        runSearch();
                } catch (Exception ie) {
                    break;
                }
            }
        }

        private void runSearch() throws IOException, ParseException, SpotifyWebApiException {
            Track[] items = api.searchTracks(search).build().execute().getItems();
            List<Track> results = Arrays.stream(items).sequential().collect(Collectors.toList());
            String thisSearch = search;
            search = nextSearch;
            nextSearch = "";
            onSearchCompleted(results, thisSearch);
        }
    }

    public interface OnSearchResult {

        void onSearchResult(@NonNull List<Track> result, @NonNull String search);

    }
}
