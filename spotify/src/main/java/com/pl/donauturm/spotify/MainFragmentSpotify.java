package com.pl.donauturm.spotify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;
import com.pl.donauturm.spotify.view.SmallPlayerView;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragmentSpotify#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragmentSpotify extends Fragment implements SearchView.OnQueryTextListener,
        SearchAdapter.OnActionListener, SearchAdapter.Queueable, MenuItem.OnActionExpandListener,
        SearchAdapter.SearchTypeAdapter.OnItemClickListener {

    private static MainFragmentSpotify LAST_INSTANCE;

    private static final String CLIENT_ID1 = "13a80ef7d1b14ad6a7d1839b910c15c4";
    private static final String CLIENT_ID2 = "fa1a2a79bbc14d97aeeffe5f6ec03607";
    private static final String SECRET_1 = "ede4d8242cf1461e868296cff140a192"; // TODO: <- remove this
    private static final String SECRET_2 = "b5cbbe7a5d774411b892677db727fda6"; // TODO: <- remove this
    private static final String REDIRECT_URI1 = "https://spotify_donauturm_pl.com/auth1";
    private static final String REDIRECT_URI2 = "https://spotify_donauturm_pl.com/auth2";
    private static final String USERNAME1 = "spotify1@donauturm.eu";
    private static final String USERNAME2 = "spotify2@donauturm.eu";

    private final SpotifyAuthHelper authHelper1 = new SpotifyAuthHelper(CLIENT_ID1, SECRET_1, REDIRECT_URI1, USERNAME1);
    private final SpotifyAuthHelper authHelper2 = new SpotifyAuthHelper(CLIENT_ID2, SECRET_2, REDIRECT_URI2, USERNAME2);
    private SpotifyApi api1;
    private SpotifyApi api2;
    private RepeatingPlaybackRefresh<String> playbackRefresh1;
    private RepeatingPlaybackRefresh<String> playbackRefresh2;
    private String lastSearch;
    private Handler searchDelayHandler;
    private SearchAdapter<?, ?> adapter;
    private SearchAdapter.SearchTypeAdapter typeAdapter;
    private SearchAdapter.SearchTypeAdapter.SearchType currentPage;

    private WebView mWebView;
    private ConstraintLayout mRoot;
    private RecyclerView mRecyclerView;
    private RecyclerView mTypeView;
    private SmallPlayerView mSmallPlayerView1;
    private SmallPlayerView mSmallPlayerView2;

    public MainFragmentSpotify() {
        // Required empty public constructor
        LAST_INSTANCE = this;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MainFragmentSpotify.
     */
    @NonNull
    @Contract(" -> new")
    @Keep
    @SuppressWarnings("unused")
    public static MainFragmentSpotify newInstance() {
        return new MainFragmentSpotify();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHasOptionsMenu(true);
    }

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_spotify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        mWebView = root.findViewById(R.id.web_view);
        mSmallPlayerView1 = root.findViewById(R.id.player_1);
        mSmallPlayerView2 = root.findViewById(R.id.player_2);
        mRecyclerView = root.findViewById(R.id.recycler);
        mTypeView = root.findViewById(R.id.types);
        mRoot = root.findViewById(R.id.root_constraint);

        authHelper1.setAndInitWebView(mWebView);
        authHelper2.setWebView(mWebView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new SearchAdapterTrack(mRecyclerView, new ArrayList<>());
        adapter.addOnActionListener(this);
        adapter.setQueueableCheck(this);
        mRecyclerView.setAdapter(adapter);
        if (mRecyclerView.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        mTypeView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        typeAdapter = new SearchAdapter.SearchTypeAdapter();
        typeAdapter.addOnItemClickListener(this);
        mTypeView.setAdapter(typeAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        authenticate1();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.spotify_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchItem.setOnActionExpandListener(this);
    }

    private void authenticate1() {
        authHelper1.authenticate(api -> {
            this.api1 = api;
            authenticate2();
        });
    }

    private void authenticate2() {
        authHelper2.setAndInitWebView(mWebView);
        authHelper2.authenticate(api -> {
            this.api2 = api;
            connected();
        });
    }

    private void connected() {
        initSmallPlayer(mSmallPlayerView1, api1, USERNAME1);
        initSmallPlayer(mSmallPlayerView2, api2, USERNAME2);
        playbackRefresh1 = new RepeatingPlaybackRefresh<>(USERNAME1, managePlaybackCallback());
        playbackRefresh2 = new RepeatingPlaybackRefresh<>(USERNAME2, managePlaybackCallback());
        playbackRefresh1.startRefresh(api1);
        playbackRefresh2.startRefresh(api2);
        showToast("Connected to both accounts");
    }

    private void initSmallPlayer(@NonNull SmallPlayerView smallPlayerView, SpotifyApi api, String account) {
        smallPlayerView.setAccount(account);
        smallPlayerView.postNotifyDataChanged();
        smallPlayerView.setOnClickListener(v -> showPlayer(api, account));
        smallPlayerView.setOnPauseClickedListener(v -> api.pauseUsersPlayback().build().executeAsync());
        smallPlayerView.setOnPlayClickedListener(v -> api.startResumeUsersPlayback().build().executeAsync());
        smallPlayerView.setOnSkipClickedListener(v -> api.skipUsersPlaybackToNextTrack().build().executeAsync());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query == null) return false;
        if (query.trim().isEmpty()) return false;
        if (query.equals(lastSearch)) return false;
        lastSearch = query;
        search(lastSearch);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (searchDelayHandler == null) searchDelayHandler = new Handler();
        searchDelayHandler.removeCallbacksAndMessages(null);
        searchDelayHandler.postDelayed(() -> onQueryTextSubmit(newText), 1000);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        mTypeView.setVisibility(View.VISIBLE);
        RecyclerView.Adapter<?> adapter = mTypeView.getAdapter();
        if (adapter instanceof SearchAdapter.SearchTypeAdapter) {
            SearchAdapter.SearchTypeAdapter searchTypeAdapter = (SearchAdapter.SearchTypeAdapter) adapter;
            searchTypeAdapter.setSelected(0);
            searchTypeAdapter.notifyItemRangeChanged(0, searchTypeAdapter.getItemCount());
            currentPage = searchTypeAdapter.getSelected();
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mTypeView.setVisibility(View.GONE);
        adapter.clear();
        currentPage = null;
        return true;
    }

    private void search(String search) {
        switch (typeAdapter.getSelected()) {
            case TRACK:
                searchTracks(search);
                break;
            case PLAYLIST:
                searchPlaylists(search);
                break;
            default:
                adapter.clear();
        }
    }

    private void searchTracks(String search) {
        api1.searchTracks(search).limit(30).build().executeAsync().whenComplete((trackPaging, throwable)
                -> new Handler(Looper.getMainLooper()).post(() -> onSearchResult(trackPaging.getItems())));
    }

    private void searchPlaylists(String search) {
        api1.searchPlaylists(search).limit(30).build().executeAsync().whenComplete((trackPaging, throwable)
                -> new Handler(Looper.getMainLooper()).post(() -> onSearchResult(trackPaging.getItems())));
    }

    private void onSearchResult(@NonNull Track[] tracks) {
        if (currentPage != SearchAdapter.SearchTypeAdapter.SearchType.TRACK) return;
        final SearchAdapterTrack adapterTrack;
        if (adapter instanceof SearchAdapterTrack) adapterTrack = ((SearchAdapterTrack) adapter);
        else {
            adapter = adapterTrack = new SearchAdapterTrack(mRecyclerView, new ArrayList<>());
            adapter.addOnActionListener(this);
            adapter.setQueueableCheck(this);
            if (adapter.requiresApi()) adapter.setSearchApi(api1);
            mRecyclerView.setAdapter(adapter);
        }
        List<SearchAdapterTrack.ImageTrack> imageTracks = new ArrayList<>();
        for (int i = 0; i < tracks.length; i++) {
            Track track = tracks[i];
            SearchAdapterTrack.ImageTrack imageTrack = new SearchAdapterTrack.ImageTrack(track);
            imageTrack.loadBitmap((bitmap, item, data) -> adapterTrack.updateItem(item, data), imageTrack, i);
            imageTracks.add(imageTrack);
        }
        adapterTrack.clear();
        adapterTrack.addItems(imageTracks);
    }

    private void onSearchResult(@NonNull PlaylistSimplified[] playlists) {
        if (currentPage != SearchAdapter.SearchTypeAdapter.SearchType.PLAYLIST) return;
        final SearchAdapterPlaylist adapterPlaylist;
        if (adapter instanceof SearchAdapterPlaylist)
            adapterPlaylist = ((SearchAdapterPlaylist) adapter);
        else {
            adapter = adapterPlaylist = new SearchAdapterPlaylist(mRecyclerView, new ArrayList<>());
            adapter.addOnActionListener(this);
            adapter.setQueueableCheck(this);
            if (adapter.requiresApi()) adapter.setSearchApi(api1);
            mRecyclerView.setAdapter(adapter);
        }
        adapterPlaylist.clear();
        List<SearchAdapterPlaylist.ImagePlaylist> imageTracks = new ArrayList<>();
        for (int i = 0; i < playlists.length; i++) {
            PlaylistSimplified playlist = playlists[i];
            SearchAdapterPlaylist.ImagePlaylist imageTrack = new SearchAdapterPlaylist.ImagePlaylist(playlist);
            imageTrack.loadBitmap((bitmap, item, data) -> adapterPlaylist.updateItem(item, data), imageTrack, i);
            imageTracks.add(imageTrack);
        }
        adapterPlaylist.addItems(imageTracks);
    }

    @Override
    public void onTypeItemChanged(int index, SearchAdapter.SearchTypeAdapter.SearchType type) {
        adapter.clear();
        currentPage = type;
        search(lastSearch);
    }

    private void showPlayer(SpotifyApi api, @NonNull String account) {
        RepeatingPlaybackRefresh<String> refresher = (account.equals(USERNAME1) ? playbackRefresh1 : playbackRefresh2);
        SpotifyPlayerFragment spotifyPlayerFragment = SpotifyPlayerFragment.newInstance(api, refresher, account);
        spotifyPlayerFragment.show(getParentFragmentManager(), getTag());
    }

    @NonNull
    @Contract(" -> new")
    private RepeatingPlaybackRefresh.PlaybackUpdater<String> managePlaybackCallback() {
        return new RepeatingPlaybackRefresh.PlaybackUpdater<>() {
            @Override
            public void onActiveUpdate(boolean active, Track track, RepeatingPlaybackRefresh.PlaybackState state, String username) {
                SmallPlayerView spv = username.equals(USERNAME1) ? mSmallPlayerView1 : mSmallPlayerView2;
                if (active && !spv.isActive()) {
                    spv.setActive(true);
                    spv.postNotifyDataChanged();
                }
            }

            @Override
            public void onPlaybackUpdateRepeatingError(int errorCount, Throwable throwable, String username) {
                SmallPlayerView spv = username.equals(USERNAME1) ? mSmallPlayerView1 : mSmallPlayerView2;
                spv.setActive(false);
                spv.postNotifyDataChanged();
            }

            @Override
            public void onPlayingUpdate(boolean playing, Track track, RepeatingPlaybackRefresh.PlaybackState state, String username) {
                SmallPlayerView spv = username.equals(USERNAME1) ? mSmallPlayerView1 : mSmallPlayerView2;
                spv.setPlaying(playing);
                spv.postNotifyDataChanged();
            }

            @Override
            public void onTrackNameUpdate(String trackName, Track track, RepeatingPlaybackRefresh.PlaybackState state, String username) {
                SmallPlayerView spv = username.equals(USERNAME1) ? mSmallPlayerView1 : mSmallPlayerView2;
                spv.setTrackName(trackName);
                spv.postNotifyDataChanged();
                if (getTrackImageUrl(track) != null) {
                    new BitmapDownloadRequest(getTrackImageUrl(track)).requestAsync(bitmap -> {
                        spv.setAlbumImage(bitmap);
                        spv.postNotifyDataChanged();
                    });
                }
            }
        };
    }

    @Nullable
    private String getTrackImageUrl(Track track) {
        if (track == null) return null;
        AlbumSimplified album = track.getAlbum();
        if (album == null) return null;
        Image[] images = album.getImages();
        if (images == null) return null;
        if (images.length <= 0) return null;
        return images[0].getUrl();
    }


    @Override
    public void onQueueClicked(Track track, int player, String account) {
        SpotifyApi api = player == 1 ? api1 : api2;
        api.addItemToUsersPlaybackQueue(track.getUri()).build().executeAsync().whenComplete((s, throwable)
                -> mRoot.post(()
                -> showToast(track.getName() + " has been added to the queue")));
    }

    @Override
    public boolean isQueueable(@Nullable Track track, int i, String account) {
        SmallPlayerView spv = i == 1 ? mSmallPlayerView1 : mSmallPlayerView2;
        return spv.isActive();
    }

    private void showRecommendations() {

    }

    public static void tryShowToast(String text) {
        try {
            if (LAST_INSTANCE != null)
                LAST_INSTANCE.showToast(text);
        } catch (Exception ignored) {

        }
    }

    public void showToast(String text) {
        showToast(text, mRoot);
    }

    private void showToast(String text, ConstraintLayout layout) {
        TextView snackText = layout.findViewById(R.id.snackbar).findViewById(R.id.text);
        snackText.setText(text);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.clear(R.id.snackbar, ConstraintSet.TOP);
        constraintSet.connect(R.id.snackbar, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(1200);
        TransitionManager.beginDelayedTransition(layout, transition);
        constraintSet.applyTo(layout);
        layout.postDelayed(() -> hideToast(layout), 1000 * 3);
    }

    private void hideToast(ConstraintLayout layout) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.clear(R.id.snackbar, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.snackbar, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(1200);
        TransitionManager.beginDelayedTransition(layout, transition);
        constraintSet.applyTo(layout);
    }
}