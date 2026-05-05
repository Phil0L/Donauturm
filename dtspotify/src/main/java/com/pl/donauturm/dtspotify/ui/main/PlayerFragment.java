package com.pl.donauturm.dtspotify.ui.main;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pl.donauturm.commons.util.ThemeManager;
import com.pl.donauturm.commons.util.log.Logging;
import com.pl.donauturm.dtspotify.DTSpotify;
import com.pl.donauturm.dtspotify.R;
import com.pl.donauturm.dtspotify.commons.DurationFormatter;
import com.pl.donauturm.dtspotify.commons.OnEditTextChanged;
import com.pl.donauturm.dtspotify.commons.lyrics.Lyrics;
import com.pl.donauturm.dtspotify.commons.lyrics.LyricsManager;
import com.pl.donauturm.dtspotify.databinding.FragmentPlayerBinding;
import com.pl.donauturm.dtspotify.ui.volume.VolumeDialog;
import com.pl.donauturm.spotify.RepeatingPlaybackRefresh;
import com.pl.donauturm.spotify.RepeatingQueueRefresh;
import com.pl.donauturm.spotify.SpotifyAuthHelper;
import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * A fragment containing the spotify player
 */
public class PlayerFragment extends Fragment implements
        RepeatingPlaybackRefresh.DefaultPlaybackUpdate<String>,
        RepeatingQueueRefresh.DefaultQueueUpdate<String>,
        SearchManager.OnSearchResult, VolumeDialog.OnVolumeSliderChanged,
        LyricsManager.OnLyricsChange, ThemeManager.OnThemSwitch {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_AUTH_CLIENT_ID = "client_id";
    private static final String ARG_AUTH_CLIENT_SECRET = "client_secret";
    private static final String ARG_AUTH_REDIRECT = "redirect";
    private static final String ARG_AUTH_USERNAME = "username";
    private final String accountName;
    private final MutableLiveData<ConnectionState> connectionState = new MutableLiveData<>(ConnectionState.UNKNOWN);
    private final VolumeDialog volumeDialog;
    private final LyricsManager lyricsManager;
    private String searchInput;
    private SearchManager searchManager;
    private PlayerViewModel pageViewModel;
    private QueueAdapter queueAdapter;
    private SearchAdapter searchAdapter;
    private LyricsAdapter lyricsAdapter;
    private LinearLayoutManager lyricsLayoutManager;
    private RepeatingPlaybackRefresh<String> playbackRefresh;
    private RepeatingQueueRefresh<String> queueRefresh;
    private FragmentPlayerBinding binding;
    private ThemeManager themeManager;

    public PlayerFragment() {
        this("");
    }

    public PlayerFragment(String accountName) {
        this.volumeDialog = VolumeDialog.newInstance();
        this.volumeDialog.onVolumeSliderChanged(this);
        this.lyricsManager = new LyricsManager();
        this.lyricsManager.onLyricChange(this);
        this.accountName = accountName;
        this.searchInput = "";
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
    }

    @NonNull
    public static PlayerFragment newInstance(int index, @NonNull SpotifyAuthHelper.AuthData authData) {
        PlayerFragment fragment = new PlayerFragment(authData.username);
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putString(ARG_AUTH_CLIENT_ID, authData.clientID);
        bundle.putString(ARG_AUTH_CLIENT_SECRET, authData.clientSecret);
        bundle.putString(ARG_AUTH_REDIRECT, authData.redirectUri);
        bundle.putString(ARG_AUTH_USERNAME, authData.username);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = getArguments() == null ? 1 : getArguments().getInt(ARG_SECTION_NUMBER);
        onCreateViewModel(index);
    }

    public void onCreateViewModel(int index) {
        pageViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        pageViewModel.setIndex(index);
        pageViewModel.connection().observe(requireActivity(), connectionState::setValue);
        pageViewModel.connection().observe(requireActivity(), pageViewModel::onConnectionStateChanged);
        pageViewModel.spotifyApi().observe(requireActivity(), /*if (api != null)*/ this::onSpotifyApiReady);
    }

    private void onSpotifyApiReady(SpotifyApi spotifyApi) {
        playbackRefresh = new RepeatingPlaybackRefresh<>(accountName, this);
        playbackRefresh.setFrequency(1000, TimeUnit.MILLISECONDS);
        playbackRefresh.startRefresh(spotifyApi);
        queueRefresh = new RepeatingQueueRefresh<>(accountName, this);
        queueRefresh.setFrequency(2, TimeUnit.SECONDS);
        queueRefresh.startRefresh(spotifyApi);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        pageViewModel.createSpotifyApi(readAuthData(), binding.webview);
        binding.textInput.addTextChangedListener((OnEditTextChanged) this::onSearchTextChanged);
        binding.searchIcon.setOnClickListener(v -> clearSearch());
        binding.includeQueue.queue.setAdapter(queueAdapter = new QueueAdapter());
        binding.includeQueue.queue.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.includeSearch.searchRecycler.setAdapter(searchAdapter = new SearchAdapter());
        binding.includeSearch.searchRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.includeLyrics.lyricsRecycler.setAdapter(lyricsAdapter = new LyricsAdapter());
        binding.includeLyrics.lyricsRecycler.setLayoutManager(lyricsLayoutManager = new LinearLayoutManager(requireContext()));
        binding.includeQueue.includePlaying.skipButton.setOnClickListener(v -> this.onSkipClicked());
        binding.includeQueue.includePlaying.playPauseButton.setOnClickListener(v -> this.onPlayPauseClicked());
        binding.includeQueue.includePlaying.playingImageCard.setOnClickListener(v -> this.onLyricsClicked());
        return binding.getRoot();
    }

    @Nullable
    private SpotifyAuthHelper.AuthData readAuthData() {
        return (getArguments() == null) ? null :
                new SpotifyAuthHelper.AuthData(
                        getArguments().getString(ARG_AUTH_CLIENT_ID),
                        getArguments().getString(ARG_AUTH_CLIENT_SECRET),
                        getArguments().getString(ARG_AUTH_REDIRECT),
                        getArguments().getString(ARG_AUTH_USERNAME));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pageViewModel.view().observe(getViewLifecycleOwner(), viewState ->
                updateViews(viewState == ViewState.DISCONNECTED, viewState == ViewState.QUEUE, viewState == ViewState.SEARCH, pageViewModel.lyrics().getValue() == ViewState.Lyrics.SHOWN));
        pageViewModel.lyrics().observe(getViewLifecycleOwner(), lyricsState ->
                updateViews(pageViewModel.view().getValue() == ViewState.DISCONNECTED, pageViewModel.view().getValue() == ViewState.QUEUE, pageViewModel.view().getValue() == ViewState.SEARCH, lyricsState == ViewState.Lyrics.SHOWN));
    }

    public void updateViews(boolean showDisconnect, boolean showQueue, boolean showSearch, boolean showLyrics) {
        binding.includeDiscon.getRoot().setVisibility(showDisconnect ? View.VISIBLE : View.GONE);
        binding.includeQueue.getRoot().setVisibility(showQueue ? View.VISIBLE : View.GONE);
        binding.includeSearch.getRoot().setVisibility(showSearch ? View.VISIBLE : View.GONE);
        binding.includeLyrics.getRoot().setVisibility(showLyrics ? View.VISIBLE : View.GONE);
        binding.textInput.setEnabled(!showDisconnect && !showLyrics);
        binding.searchRoot.setVisibility(showDisconnect || showLyrics ? View.INVISIBLE : View.VISIBLE);
        binding.searchIcon.setVisibility(showDisconnect || showLyrics ? View.INVISIBLE : View.VISIBLE);
    }

    public boolean onBackPressed() {
        if (pageViewModel.view().getValue() == ViewState.SEARCH) {
            binding.textInput.setText("");
            return true;
        }
        return false;
    }

    public boolean onVolumePressed(int button, int action) {
        final int VOLUME_INCREMENT = 5;
        if (pageViewModel.connection().getValue() != ConnectionState.CONNECTED)
            return false;
        RepeatingPlaybackRefresh.PlaybackState playbackState = pageViewModel.playbackState().getValue();
        if (playbackState == null)
            return false;
        if (action == KeyEvent.ACTION_UP)
            return true;
        Integer volume = playbackState.getVolume();
        volumeDialog.show(requireActivity().getSupportFragmentManager(), getTag());
        if (button == KeyEvent.KEYCODE_VOLUME_UP)
            updateVolume(volume + VOLUME_INCREMENT, true);
        if (button == KeyEvent.KEYCODE_VOLUME_DOWN)
            updateVolume(volume - VOLUME_INCREMENT, true);
        return true;
    }

    public boolean onVolumePressed(int oldVolume, int button, int action) {
        final int VOLUME_INCREMENT = 5;
        if (pageViewModel.connection().getValue() != ConnectionState.CONNECTED)
            return false;
        if (action == KeyEvent.ACTION_UP)
            return true;
        volumeDialog.show(requireActivity().getSupportFragmentManager(), getTag());
        if (button == KeyEvent.KEYCODE_VOLUME_UP)
            updateVolume(oldVolume + VOLUME_INCREMENT, true);
        if (button == KeyEvent.KEYCODE_VOLUME_DOWN)
            updateVolume(oldVolume - VOLUME_INCREMENT, true);
        return true;
    }

    private void updateVolume(int newVolume, boolean keepDialogAlive) {
        SpotifyApi spotifyApi = pageViewModel.spotifyApi().getValue();
        if (spotifyApi == null)
            return;
        newVolume = Math.min(100, Math.max(0, newVolume));
        spotifyApi.setVolumeForUsersPlayback(newVolume).build().executeAsync();
        volumeDialog.setVolume(newVolume, keepDialogAlive);
    }

    @Override
    public void onVolumeSliderChanged(int newVolume) {
        updateVolume(newVolume, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (playbackRefresh != null)
            playbackRefresh.cancel();
        if (queueRefresh != null)
            queueRefresh.cancel();
    }

    private void onSearchTextChanged(@NonNull Editable s) {
        pageViewModel.onSearchTextChanged(s.toString());
        if (searchInput.isEmpty() && !s.toString().isEmpty())
            activateSearch();
        if (!searchInput.isEmpty() && s.toString().isEmpty())
            deactivateSearch();
        searchInput = s.toString();
        if (searchManager != null)
            searchManager.searchChanged(searchInput);
    }

    private void activateSearch() {
        searchManager = new SearchManager(pageViewModel.spotifyApi().getValue());
        searchManager.onSearchCompleted(this);
        searchAdapter.onQueueTrackClicked(this::queueTrack);
        binding.searchIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close, null));
    }

    private void deactivateSearch() {
        searchManager.cancel();
        searchAdapter.updateSearchItems(new ArrayList<>());
        binding.searchIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search, null));
    }

    @Override
    public void onSearchResult(@NonNull List<Track> result, @NonNull String search) {
        searchAdapter.updateSearchItems(result);
    }

    public void clearSearch() {
        binding.textInput.setText("");
    }

    public void queueTrack(@NonNull String trackId) {
        final SpotifyApi api;
        if ((api = pageViewModel.spotifyApi().getValue()) != null)
            api.addItemToUsersPlaybackQueue("spotify:track:" + trackId).build().executeAsync();
    }

    /**
     * passthrough to {@link PlayerStateManager}
     *
     * @return LiveData of the connection state
     */
    @NonNull
    public LiveData<ConnectionState> getConnectionState() {
        return connectionState;
    }

    @Override
    public void onAnyPlaybackUpdate(RepeatingPlaybackRefresh.PlaybackState state, String id) {
        new Handler(Looper.getMainLooper()).post(() -> pageViewModel.setPlaybackState(state));
    }

    @Override
    public void onActiveUpdate(boolean active, Track track, RepeatingPlaybackRefresh.PlaybackState state, String username) {
        new Handler(Looper.getMainLooper()).post(() -> pageViewModel.setActive(username, active, "Active update"));
    }

    @Override
    public void onPlaybackUpdateRepeatingError(int errorCount, Throwable throwable, String username) {
        new Handler(Looper.getMainLooper()).post(() -> pageViewModel.setActive(username, false, "Repeating error"));
    }

    @Override
    public void onTrackNameUpdate(String trackName, Track track, RepeatingPlaybackRefresh.PlaybackState state, String username) {
        Logging.post("Now playing: " + trackName + " on " + username);
        new Handler(Looper.getMainLooper()).post(() -> updateCurrentlyPlayingText(state));
        new Handler(Looper.getMainLooper()).post(() -> lyricsManager.updateTrack(track));
        new Handler(Looper.getMainLooper()).post(this::hideLyricsIcon);
    }

    @Override
    public void onAlbumImageUrlUpdate(String albumImageUrl, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        new Handler(Looper.getMainLooper()).post(() -> updateCurrentlyPlayingImage(state));
    }

    @Override
    public void onTrackProgressUpdate(int trackProgress, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        new Handler(Looper.getMainLooper()).post(() -> updateCurrentlyPlayingProgress(state));
        new Handler(Looper.getMainLooper()).post(() -> updateQueueDelay(state.getTrackLength() - trackProgress));
        new Handler(Looper.getMainLooper()).post(() -> lyricsManager.updateTime(trackProgress));
    }

    @Override
    public void onPlayingUpdate(boolean playing, Track track, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        new Handler(Looper.getMainLooper()).post(() -> updateCurrentlyPlayingButtons(state));
    }

    @Override
    public void onQueueUpdate(RepeatingQueueRefresh.QueueState state, String id) {
        new Handler(Looper.getMainLooper()).post(() -> pageViewModel.setQueueState(state));
    }

    @Override
    public void onQueueUpdate(List<Track> queue, List<String> queueIds, String id) {
        new Handler(Looper.getMainLooper()).post(() -> updateQueue(queue));
    }

    @Override
    public void onVolumeUpdate(int volume, RepeatingPlaybackRefresh.PlaybackState state, String id) {
        new Handler(Looper.getMainLooper()).post(() -> volumeDialog.setVolumeFromApi(volume));
    }

    public void updateCurrentlyPlayingText(RepeatingPlaybackRefresh.PlaybackState state) {
        if (binding == null) return;
        binding.includeQueue.includePlaying.playingTitle.setText(state.getTrackName());
        binding.includeQueue.includePlaying.playingArtist.setText(state.getArtistName());
        binding.includeLyrics.includePlaying.playingTitle.setText(state.getTrackName());
        binding.includeLyrics.includePlaying.playingArtist.setText(state.getArtistName());
    }

    public void updateCurrentlyPlayingImage(RepeatingPlaybackRefresh.PlaybackState state) {
        new BitmapDownloadRequest(state.getAlbumImageUrl()).requestAsync(bitmap ->
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (binding == null) return;
                    binding.includeQueue.includePlaying.playingImage.setImageBitmap(bitmap);
                    binding.includeLyrics.includePlaying.playingImage.setImageBitmap(bitmap);
                }));
    }

    public void updateCurrentlyPlayingProgress(RepeatingPlaybackRefresh.PlaybackState state) {
        if (binding == null) return;
        binding.includeQueue.includePlaying.progress.setProgress(((int) (state.getTrackPercentile() * 100)), true);
        binding.includeQueue.includePlaying.duration.setText(DurationFormatter.minSec(state.getTrackLengthRemainder(), "%02d:%02d"));
        binding.includeLyrics.includePlaying.progress.setProgress(((int) (state.getTrackPercentile() * 100)), true);
        binding.includeLyrics.includePlaying.duration.setText(DurationFormatter.minSec(state.getTrackLengthRemainder(), "%02d:%02d"));
    }

    public void updateCurrentlyPlayingButtons(RepeatingPlaybackRefresh.PlaybackState state) {
        if (binding == null) return;
        updatePlayPauseButton(state.isPlaying(false));
    }

    public void updatePlayPauseButton(boolean resumed) {
        if (binding == null) return;
        if (resumed)
            binding.includeQueue.includePlaying.playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), com.pl.donauturm.spotify.R.drawable.ic_pause, null));
        else
            binding.includeQueue.includePlaying.playPauseButton.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), com.pl.donauturm.spotify.R.drawable.ic_play, null));
    }

    public void updateQueue(List<Track> queue) {
        if (queueAdapter == null || searchAdapter == null) return;
        queueAdapter.updateQueueItems(queue);
        searchAdapter.provideCurrentQueue(queue);
    }

    public void updateQueueDelay(int trackRemaining) {
        if (queueAdapter == null) return;
        queueAdapter.updateQueueDelay(trackRemaining);
    }

    @SuppressWarnings("ConstantConditions")
    public void onPlayPauseClicked() {
        if (pageViewModel.connection().getValue() != ConnectionState.CONNECTED) return;
        if (!pageViewModel.spotifyApi().isInitialized()) return;
        if (!pageViewModel.playbackState().isInitialized()) return;
        if (!pageViewModel.playbackState().getValue().isActive(false)) return;
        boolean resumed = pageViewModel.playbackState().getValue().isPlaying(false);
        this.updatePlayPauseButton(!resumed);
        if (resumed)
            pageViewModel.spotifyApi().getValue().pauseUsersPlayback().build().executeAsync();
        else
            pageViewModel.spotifyApi().getValue().startResumeUsersPlayback().build().executeAsync();
    }

    @SuppressWarnings("ConstantConditions")
    public void onSkipClicked() {
        if (pageViewModel.connection().getValue() != ConnectionState.CONNECTED) return;
        if (!pageViewModel.spotifyApi().isInitialized()) return;
        if (!pageViewModel.playbackState().isInitialized()) return;
        if (!pageViewModel.playbackState().getValue().isActive(false)) return;
        pageViewModel.spotifyApi().getValue().skipUsersPlaybackToNextTrack().build().executeAsync();
    }

    @SuppressWarnings("ConstantConditions")
    public void onLyricsClicked(){
        if (pageViewModel.connection().getValue() != ConnectionState.CONNECTED) return;
        if (!pageViewModel.spotifyApi().isInitialized()) return;
        if (!pageViewModel.playbackState().isInitialized()) return;
        if (!pageViewModel.playbackState().getValue().isActive(false)) return;
        if (!lyricsManager.hasLyrics()) return;
        new Handler(Looper.getMainLooper()).post(this::showLyricsScreen);
    }

    public void onLyricsTimerExpired() {
        // check if lyrics are available and if so show the lyrics screen
        if (lyricsManager.hasLyrics()) {
            new Handler(Looper.getMainLooper()).post(this::showLyricsScreen);
//            new Handler(Looper.getMainLooper()).post(() -> updateLyricsScreenContent(lyricsManager.getCurrentLyrics(), lyricsManager.getCurrentTrack()));
        }
    }

    @Override
    public void onLyricsTrackChange(Lyrics lyrics, Track track, String pastLyrics, String futureLyrics) {
        // check if lyrics are available and if the timer is expired.
        // if so show the lyrics screen;
        // also update the lyrics.
        // this will only be visible if the lyrics screen is already showing.
        if (lyricsManager.hasLyrics() && requireActivity(DTSpotify.class).isLyricsTimerExpired() && requireActivity(DTSpotify.class).getCurrentPlayer() == this)
            new Handler(Looper.getMainLooper()).post(this::showLyricsScreen);
        new Handler(Looper.getMainLooper()).post(() -> updateLyricsScreenContent(lyrics));
        if (lyrics == null || lyrics.isError())
            new Handler(Looper.getMainLooper()).post(this::hideLyricsIcon);
        else
            new Handler(Looper.getMainLooper()).post(this::showLyricsIcon);
    }

    @Override
    public void onLyricsChange(Lyrics lyrics, Track track, String pastLyrics, String futureLyrics) {

    }

    @Override
    public void onLyricsTimeChange(Lyrics lyrics, Track track, long time) {
        new Handler(Looper.getMainLooper()).post(() -> updateLyricsTime(lyrics, time));
    }

    private void updateLyricsTime(Lyrics ignoredLyrics, long time) {
        if (binding == null || lyricsAdapter == null || lyricsLayoutManager == null) return;
        lyricsAdapter.updateProgress(time);
        lyricsLayoutManager.scrollToPositionWithOffset(
                lyricsAdapter.getPresentIndex() + 1,
                binding.includeLyrics.lyricsRecycler.getHeight() / 2);
    }

    private void updateLyricsScreenContent(Lyrics lyrics) {
        lyricsAdapter.updateLyricsContent(lyrics);
        lyrics.getReplacementImage(bitmap -> binding.includeLyrics.background.setImageBitmap(bitmap));

    }

    private void showLyricsScreen() {
        if (pageViewModel == null) return;
        pageViewModel.showLyrics();
    }

    public void hideLyricsScreen() {
        if (pageViewModel == null) return;
        pageViewModel.hideLyrics();
    }

    private void showLyricsIcon() {
        if (binding == null) return;
        binding.includeQueue.includePlaying.lyricsIcon.setVisibility(View.VISIBLE);
        binding.includeQueue.includePlaying.lyricsIconFade.setVisibility(View.VISIBLE);
    }

    public void hideLyricsIcon() {
        if (binding == null) return;
        binding.includeQueue.includePlaying.lyricsIcon.setVisibility(View.GONE);
        binding.includeQueue.includePlaying.lyricsIconFade.setVisibility(View.GONE);
    }

    public <A> A requireActivity(@NonNull Class<A> aClass) {
        return aClass.cast(requireActivity());
    }

    @Override
    public void onThemeSwitch(ThemeManager.Theme theme) {
        if (binding == null || DTSpotify.inDayNightReload) return;

        // DISCONNECTED
        binding.includeDiscon.message.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeDiscon.shrugging.setColorFilter(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary), PorterDuff.Mode.SRC_IN);

        // QUEUE
        binding.includeQueue.includePlaying.playingCard.setCardBackgroundColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground));
        binding.includeQueue.includePlaying.playingTitle.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeQueue.includePlaying.playingArtist.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeQueue.includePlaying.duration.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeQueue.includePlaying.lyricsIcon.setColorFilter(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary), PorterDuff.Mode.SRC_IN);
        binding.includeQueue.includePlaying.lyricsIconFade.setColorFilter(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground), PorterDuff.Mode.SRC_IN);
        binding.includeQueue.includePlaying.progress.setProgressBackgroundTintList(ColorStateList.valueOf(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary)));

        //SEARCH
        binding.includeSearch.searchCard.setCardBackgroundColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground));

        // LYRICS
        binding.includeLyrics.fade.setColorFilter(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground), PorterDuff.Mode.SRC_IN);
        binding.includeLyrics.hide.setColorFilter(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground), PorterDuff.Mode.SRC_IN);
        binding.includeLyrics.bottomPlayer.setBackgroundColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground));
        binding.includeLyrics.includePlaying.playingCard.setCardBackgroundColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.colorBackground));
        binding.includeLyrics.includePlaying.playingTitle.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeLyrics.includePlaying.playingArtist.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeLyrics.includePlaying.duration.setTextColor(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary));
        binding.includeLyrics.includePlaying.progress.setProgressBackgroundTintList(ColorStateList.valueOf(ThemeManager.ThemeColor.getColor(requireContext(), android.R.attr.textColorTertiary)));
    }


    public enum ConnectionState {

        UNKNOWN(false),
        LOGGED_OUT(false),
        LOGGED_IN(true),
        DISCONNECTED(true),
        CONNECTED(true);

        final boolean loggedIn;

        ConnectionState(boolean loggedIn) {
            this.loggedIn = loggedIn;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }

    }

    public enum ViewState {
        DISCONNECTED,
        QUEUE,
        SEARCH;

        public enum Lyrics {
            SHOWN,
            HIDDEN
        }
    }
}