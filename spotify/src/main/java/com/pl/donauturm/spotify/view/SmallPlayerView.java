package com.pl.donauturm.spotify.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AnyThread;
import androidx.annotation.WorkerThread;
import androidx.core.content.res.ResourcesCompat;

import com.pl.donauturm.spotify.R;
import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Image;

public class SmallPlayerView extends FrameLayout {

    private boolean active;
    private boolean playing;
    private String account;
    private String trackName;
    private Bitmap albumImage;
    private OnClickListener playClicked;
    private OnClickListener pauseClicked;
    private OnClickListener skipClicked;

    private final TextView accountView;
    private final TextView trackNameView;
    private final ImageView albumImageView;
    private final ImageView playButton;
    private final ImageView skipButton;


    public SmallPlayerView(Context context) {
        this(context, null);
    }

    public SmallPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View view = inflate(getContext(), R.layout.prefab_player_small, this);
        accountView = view.findViewById(R.id.account);
        trackNameView = view.findViewById(R.id.track);
        albumImageView = view.findViewById(R.id.album_image);
        playButton = view.findViewById(R.id.play_pause);
        skipButton = view.findViewById(R.id.skip);
        playButton.setOnClickListener(this::onPlayClicked);
        skipButton.setOnClickListener(this::onSkipClicked);
    }

    public void setAccount(String player) {
        this.account = player;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setAlbumImage(Bitmap image) {
        this.albumImage = image;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public void setOnPlayClickedListener(OnClickListener playClicked) {
        this.playClicked = playClicked;
    }

    public void setOnPauseClickedListener(OnClickListener pauseClicked) {
        this.pauseClicked = pauseClicked;
    }

    public void setOnSkipClickedListener(OnClickListener skipClicked) {
        this.skipClicked = skipClicked;
    }

    private void onPlayClicked(View v) {
        if (active && playClicked != null)
            if (playing) {
                pauseClicked.onClick(v);
                setPlaying(false);
                notifyDataChanged();
            } else {
                playClicked.onClick(v);
                setPlaying(true);
                notifyDataChanged();
            }
    }

    private void onSkipClicked(View v) {
        if (active && skipClicked != null)
            skipClicked.onClick(v);
    }

    public void set(CurrentlyPlayingContext playingContext) {
        if (playingContext == null) {
            setActive(false);
            return;
        }
        setTrackName(playingContext.getItem().getName());
        setPlaying(playingContext.getIs_playing());
    }

    public void set(CurrentlyPlaying playing) {
        if (playing == null) {
            setActive(false);
            return;
        }
        setPlaying(playing.getIs_playing());
        setTrackName(playing.getItem().getName());
    }

    @AnyThread
    public void postNotifyDataChanged() {
        new Handler(Looper.getMainLooper()).post(this::notifyDataChanged);
    }

    @WorkerThread
    public void notifyDataChanged() {
        accountView.setText(account != null ? account : "");
        trackNameView.setText(active && trackName != null ? trackName : "Not playing");
        playButton.setImageDrawable(active ? playing ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        skipButton.setImageDrawable(active ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_skip_next, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        if (active && albumImage != null)
            albumImageView.setImageBitmap(albumImage);
        else
            albumImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_logo_corporate, null));
    }

    public void issueUpdate(SpotifyApi api) {
        // paused & track name
        api.getUsersCurrentlyPlayingTrack().build().executeAsync().whenComplete(((currentlyPlaying, throwable) -> {
            set(currentlyPlaying);
            postNotifyDataChanged();
            // album image
            api.getTrack(currentlyPlaying.getItem().getId()).build().executeAsync().whenComplete(((track, throwable1) -> {
                if (track == null) {
                    setAlbumImage(null);
                    return;
                }
                Image[] images = track.getAlbum().getImages();
                if (images != null && images.length > 0) {
                    Image image = images[0];
                    new BitmapDownloadRequest(image.getUrl()).requestAsync(bitmap -> {
                        setAlbumImage(bitmap);
                        postNotifyDataChanged();
                    });
                }
            }));
        }));
    }
}