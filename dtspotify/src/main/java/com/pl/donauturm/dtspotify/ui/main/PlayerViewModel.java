package com.pl.donauturm.dtspotify.ui.main;

import android.webkit.WebView;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pl.donauturm.commons.util.log.Logging;
import com.pl.donauturm.spotify.RepeatingPlaybackRefresh;
import com.pl.donauturm.spotify.RepeatingQueueRefresh;
import com.pl.donauturm.spotify.SpotifyAuthHelper;

import se.michaelthelin.spotify.SpotifyApi;

public class PlayerViewModel extends ViewModel {

    private final MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private final MutableLiveData<SpotifyApi> mApi = new MutableLiveData<>();
    private final MutableLiveData<RepeatingPlaybackRefresh.PlaybackState> mPlaybackState = new MutableLiveData<>();
    private final MutableLiveData<RepeatingQueueRefresh.QueueState> mQueueState = new MutableLiveData<>();
    private final MutableLiveData<PlayerFragment.ConnectionState> mConnectionState = new MutableLiveData<>(PlayerFragment.ConnectionState.UNKNOWN);
    private final MutableLiveData<PlayerFragment.ViewState> mViewState = new MutableLiveData<>(PlayerFragment.ViewState.DISCONNECTED);
    private final MutableLiveData<PlayerFragment.ViewState.Lyrics> mLyricsState = new MutableLiveData<>(PlayerFragment.ViewState.Lyrics.HIDDEN);
    private final SpotifyAuthLogger authLogger = new SpotifyAuthLogger();

    /**
     * ########## INDEX ##########
     */
    @MainThread
    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    /**
     * ######### SPOTIFY API #########
     */
    public void createSpotifyApi(@Nullable SpotifyAuthHelper.AuthData authData, WebView webView) {
        if (authData == null) return;
        createSpotifyApi(authData.clientID, authData.clientSecret, authData.redirectUri, authData.username, webView);
    }

    public void createSpotifyApi(String clientID, String clientSecret, String redirectUri, String username, WebView webView) {
        authLogger.authStart(username);
        SpotifyAuthHelper authHelper1 = new SpotifyAuthHelper(clientID, clientSecret, redirectUri, username);
        authHelper1.setAndInitWebView(webView);
        authHelper1.authenticate(() -> needsLogin(username), api -> spotifyApiCreated(api, username));
    }

    @MainThread
    public void setPlaybackState(RepeatingPlaybackRefresh.PlaybackState playbackState) {
        if (mPlaybackState == null) return;
        mPlaybackState.setValue(playbackState);
    }

    @MainThread
    public void setQueueState(RepeatingQueueRefresh.QueueState queueState) {
        mQueueState.setValue(queueState);
    }

    @MainThread
    public void spotifyApiCreated(SpotifyApi api, String username) {
        mApi.setValue(api);
        loggedIn(username);
    }

    public LiveData<SpotifyApi> spotifyApi() {
        return mApi;
    }

    public LiveData<RepeatingPlaybackRefresh.PlaybackState> playbackState() {
        return mPlaybackState;
    }

    public LiveData<RepeatingQueueRefresh.QueueState> queueState() {
        return mQueueState;
    }

    /**
     * ######### CONNECTION STATE #########
     */
    @MainThread
    public void needsLogin(String username) {
        mConnectionState.setValue(PlayerFragment.ConnectionState.LOGGED_OUT);
        authLogger.authLoggedOut(username);
    }

    @MainThread
    public void loggedIn(String username) {
        mConnectionState.setValue(PlayerFragment.ConnectionState.LOGGED_IN);
        authLogger.authLoggedIn(username);
    }

    @MainThread
    public void setActive(String username, boolean active, String cause) {
        if (mConnectionState == null) return;
        boolean currentlyConnected = mConnectionState.getValue() == PlayerFragment.ConnectionState.CONNECTED;
        boolean currentlyDisconnected = mConnectionState.getValue() == PlayerFragment.ConnectionState.DISCONNECTED;
        mConnectionState.setValue(active ? PlayerFragment.ConnectionState.CONNECTED : PlayerFragment.ConnectionState.DISCONNECTED);
        if ((currentlyConnected && !active) || (currentlyDisconnected && active) || (!currentlyConnected && !currentlyDisconnected))
            authLogger.activeChange(username, active, cause);
    }

    public LiveData<PlayerFragment.ConnectionState> connection() {
        return mConnectionState;
    }


    /**
     * ######### VIEW STATE #########
     */
    @MainThread
    public void onConnectionStateChanged(PlayerFragment.ConnectionState connectionState) {
        if (connectionState == PlayerFragment.ConnectionState.CONNECTED) {
            mViewState.setValue(PlayerFragment.ViewState.QUEUE);
        } else {
            mViewState.setValue(PlayerFragment.ViewState.DISCONNECTED);
            mLyricsState.setValue(PlayerFragment.ViewState.Lyrics.HIDDEN);
        }
    }

    @MainThread
    public void onSearchTextChanged(String text) {
        if (text.length() > 0) {
            mViewState.setValue(PlayerFragment.ViewState.SEARCH);
        } else {
            mViewState.setValue(PlayerFragment.ViewState.QUEUE);
        }
    }

    public void showLyrics(){
        mLyricsState.setValue(PlayerFragment.ViewState.Lyrics.SHOWN);
    }

    public void hideLyrics(){
        mLyricsState.setValue(PlayerFragment.ViewState.Lyrics.HIDDEN);
    }

    public LiveData<PlayerFragment.ViewState> view() {
        return mViewState;
    }

    public LiveData<PlayerFragment.ViewState.Lyrics> lyrics(){
        return mLyricsState;
    }

    public static class SpotifyAuthLogger extends Logging<String> {

        public SpotifyAuthLogger() {
            super("Spotify authentication");
        }

        public void authStart(String username) {
            log("Authentication of " + username + " started...");
        }

        public void authLoggedOut(String username) {
            log("Authentication of " + username + " needs credentials!");
        }

        public void authLoggedIn(String username) {
            log("Authentication of " + username + " successful!");
        }

        public void activeChange(String username, boolean active, String cause) {
            log("Active state of " + username + " changed. Account is now " + (active ? "active" : "inactive") + ", caused by " + cause);
        }
    }
}