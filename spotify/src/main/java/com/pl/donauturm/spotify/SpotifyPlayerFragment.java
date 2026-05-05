package com.pl.donauturm.spotify;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;
import com.pl.donauturm.spotify.view.LargePlayerView;

import java.util.List;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotifyPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotifyPlayerFragment extends BottomSheetDialogFragment implements RepeatingPlaybackRefresh.PlaybackUpdate<String> {

    private LargePlayerView largePlayerView;
    private final SpotifyApi api;
    private final RepeatingPlaybackRefresh<String> refresher;
    private final String account;

    public SpotifyPlayerFragment(SpotifyApi api, RepeatingPlaybackRefresh<String> refresher, String account) {
        this.api = api;
        this.refresher = refresher;
        this.account = account;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MainFragmentSpotify.
     */
    @Keep
    public static SpotifyPlayerFragment newInstance(SpotifyApi api, RepeatingPlaybackRefresh<String> refresher, String account) {
        return new SpotifyPlayerFragment(api, refresher, account);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setWhiteNavigationBar(dialog);
        return dialog;
    }

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_spotify_player, container, false);
        largePlayerView = root.findViewById(R.id.player_large);
        largePlayerView.setOnPauseClickedListener(this::onPauseClicked);
        largePlayerView.setOnPlayClickedListener(this::onPlayClicked);
        largePlayerView.setOnSkipClickedListener(this::onSkipClicked);
        largePlayerView.setOnPreviousClickedListener(this::onPreviousClicked);
        largePlayerView.setOnShuffleActivateClickedListener(this::onShuffleActivateClicked);
        largePlayerView.setOnShuffleDeactivateClickedListener(this::onShuffleDeactivateClicked);
        largePlayerView.setOnRepeatActivateClickedListener(this::onRepeatActivateClicked);
        largePlayerView.setOnRepeatDeactivateClickedListener(this::onRepeatDeactivateClicked);
        largePlayerView.setOnVolumeChangedListener(this::volumeChanged);
        largePlayerView.setAccount(account);
        refresher.addCallback(this);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        largePlayerView.getQueueView().setNestedScrollingEnabled(false);
        pullQueue();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refresher.removeCallback(this);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogThemeSpotify;
    }

    private void pullQueue(){
        api.getTheUsersQueue().build().executeAsync().whenComplete((playbackQueue, throwable) -> {
            List<Track> queue = playbackQueue.getQueue();
            largePlayerView.post(() -> largePlayerView.setQueue(queue));
        });
    }

    // VIEW UPDATES //

    private void onPlayClicked(View v) {
        api.startResumeUsersPlayback().build().executeAsync();
    }

    private void onPauseClicked(View v) {
        api.pauseUsersPlayback().build().executeAsync();
    }

    private void volumeChanged(SeekBar seekBar, int volume) {
        api.setVolumeForUsersPlayback(volume).build().executeAsync();
    }

    private void onRepeatDeactivateClicked(View view) {
        api.setRepeatModeOnUsersPlayback("off").build().executeAsync();
    }

    private void onRepeatActivateClicked(View view) {
        api.setRepeatModeOnUsersPlayback("context").build().executeAsync();
    }

    private void onShuffleDeactivateClicked(View view) {
        api.toggleShuffleForUsersPlayback(false).build().executeAsync();
    }

    private void onShuffleActivateClicked(View view) {
        api.toggleShuffleForUsersPlayback(true).build().executeAsync();
    }

    private void onSkipClicked(View v) {
        api.skipUsersPlaybackToNextTrack().build().executeAsync();
    }

    private void onPreviousClicked(View view) {
        api.skipUsersPlaybackToPreviousTrack().build().executeAsync();
    }

    // API UPDATES //

    @Override
    public void onAnyPlaybackUpdate(boolean active, boolean playing, boolean shuffle, boolean repeating, String trackName, String artistName, String albumName, String albumImageUrl, int trackProgress, int trackLength, int volume, String id) {
        largePlayerView.postNotifyDataChanged();
    }

    @Override
    public void onAnyPlaybackUpdate(RepeatingPlaybackRefresh.PlaybackState state, String id) {

    }

    @Override
    public void onActiveUpdate(boolean active, Track track, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        if (active && !largePlayerView.isActive()) {
            largePlayerView.setActive(true);
        }
    }

    @Override
    public void onPlayingUpdate(boolean playing, Track track, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setPlaying(playing);
    }

    @Override
    public void onShuffleUpdate(boolean shuffle, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setShuffle(shuffle);
    }

    @Override
    public void onRepeatingUpdate(boolean repeating, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setRepeating(repeating);
    }

    @Override
    public void onTrackNameUpdate(String trackName, Track track, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setTrackName(trackName);
        pullQueue();
    }

    @Override
    public void onArtistNameUpdate(String artistName, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setArtistName(artistName);
    }

    @Override
    public void onAlbumNameUpdate(String albumName, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setAlbumName(albumName);
    }

    @Override
    public void onAlbumImageUrlUpdate(String albumImageUrl, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        new BitmapDownloadRequest(albumImageUrl).requestAsync(bitmap -> {
            largePlayerView.setAlbumImage(bitmap);
            largePlayerView.postNotifyDataChanged();
        });
    }

    @Override
    public void onTrackProgressUpdate(int trackProgress, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setProgress(trackProgress);
    }

    @Override
    public void onTrackLengthUpdate(int trackLength, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setDuration(trackLength);
    }

    @Override
    public void onVolumeUpdate(int volume, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        largePlayerView.setVolume(volume);
    }

    @Override
    public void onPlaybackUpdateError(Throwable throwable, String id) {

    }

    @Override
    public void onPlaybackUpdateRepeatingError(int errorCount, Throwable throwable, String id) {
        largePlayerView.setActive(false);
    }

    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}