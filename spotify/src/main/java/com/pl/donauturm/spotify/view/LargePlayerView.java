package com.pl.donauturm.spotify.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.pl.donauturm.spotify.R;
import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequestWithContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class LargePlayerView extends FrameLayout {

    private boolean active;
    private boolean playing;
    private boolean shuffle;
    private boolean repeating;
    private String account;
    private String trackName;
    private String artistName;
    private String albumName;
    private int volume;
    private int duration;
    private int progress;
    private Bitmap albumImage;
    private List<Track> queue;
    private final List<QueueTrack> queueTracks = new ArrayList<>();
    private final Bitmap defaultImage;
    private OnClickListener playClicked;
    private OnClickListener pauseClicked;
    private OnClickListener skipClicked;
    private OnClickListener previousClicked;
    private OnClickListener shuffleDeactivateClicked;
    private OnClickListener shuffleActivateClicked;
    private OnClickListener repeatDeactivateClicked;
    private OnClickListener repeatActivateClicked;
    private OnVolumeChangedListener volumeChanged;

    private final TextView accountView;
    private final TextView trackNameView;
    private final TextView artistAlbumNameView;
    private final TextView progressView;
    private final TextView durationView;
    private final ImageView albumImageView;
    private final ImageView playButton;
    private final ImageView skipButton;
    private final ImageView previousButton;
    private final ImageView shuffleButton;
    private final ImageView repeatButton;
    private final LinearProgressIndicator progressIndicator;
    private final VerticalSeekBar volumeIndicator;
    private final RecyclerView queueView;


    public LargePlayerView(Context context) {
        this(context, null);
    }

    public LargePlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LargePlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View view = inflate(getContext(), R.layout.prefab_player_large, this);
        accountView = view.findViewById(R.id.account);
        trackNameView = view.findViewById(R.id.track);
        artistAlbumNameView = view.findViewById(R.id.artist);
        progressView = view.findViewById(R.id.timestamp);
        durationView = view.findViewById(R.id.track_length);
        albumImageView = view.findViewById(R.id.album_image);
        playButton = view.findViewById(R.id.play_pause);
        skipButton = view.findViewById(R.id.skip);
        previousButton = view.findViewById(R.id.previous);
        shuffleButton = view.findViewById(R.id.shuffle);
        repeatButton = view.findViewById(R.id.repeat);
        progressIndicator = view.findViewById(R.id.progressBar);
        volumeIndicator = view.findViewById(R.id.volume);
        queueView = view.findViewById(R.id.queue);
        queueView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        queueView.setAdapter(new QueueAdapter(queueTracks));
        playButton.setOnClickListener(this::onPlayClicked);
        skipButton.setOnClickListener(this::onSkipClicked);
        previousButton.setOnClickListener(this::onPreviousClicked);
        shuffleButton.setOnClickListener(this::onShuffleClicked);
        repeatButton.setOnClickListener(this::onRepeatingClicked);
        volumeIndicator.setOnSeekBarChangeListener((OnVolumeChangedListener) this::onVolumeChanged);
        defaultImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_logo_corporate);
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

    public void setShuffle(Boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRepeating(Boolean repeating) {
        this.repeating = repeating;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
        this.postUpdateQueueTimings();
    }

    public void setQueue(List<Track> queue){
        if (this.queue == null) this.queue = new ArrayList<>();
        else this.queue.clear();
        this.queue.addAll(queue);
        this.postUpdateQueue();
    }

    @AnyThread
    private void postUpdateQueue(){
        new Handler(Looper.getMainLooper()).post(this::updateQueue);
    }

    @WorkerThread
    private void updateQueue(){
        if (queue == null) queue = new ArrayList<>();
        List<QueueTrack> queueTracks = new ArrayList<>();
        int currentTrackRemaining = duration-progress;
        for (int i = 0; i < queue.size(); i++){
            Track track = queue.get(i);
            int delay = getTimeToPlay(i, currentTrackRemaining, queue);
            String delayS = getTimeToPlayString(delay);
            QueueTrack queueTrack = new QueueTrack(track, delayS, defaultImage);
            queueTracks.add(queueTrack);
            Pair<QueueTrack, Integer> queueTrackIntegerPair = new Pair<>(queueTrack, i);
            String url = track.getAlbum().getImages()[0].getUrl();
            new BitmapDownloadRequestWithContext<>(url, queueTrackIntegerPair).requestAsyncContext((bitmap, pair) -> {
                QueueTrack queueTrack1 = pair.first;
                int index = pair.second;
                new Handler(Looper.getMainLooper()).post(() -> {
                    queueTrack1.setImage(bitmap);
                    getQueueViewAdapter().notifyItemChanged(index);
                });
            });
        }
        this.queueTracks.clear();
        this.queueTracks.addAll(queueTracks);
        getQueueViewAdapter().notifyItemRangeChanged(0, queueTracks.size());
    }

    @AnyThread
    private void postUpdateQueueTimings(){
        new Handler(Looper.getMainLooper()).post(this::updateQueueTimings);
    }

    @WorkerThread
    private void updateQueueTimings(){
        int currentTrackRemaining = duration-progress;
        if (currentTrackRemaining < 0) return;
        for (int i = 0; i < queueTracks.size(); i++){
            QueueTrack track = queueTracks.get(i);
            int delay = getTimeToPlay(i, currentTrackRemaining, queue);
            String delayS = getTimeToPlayString(delay);
            track.setTtp(delayS);
        }
        getQueueViewAnimator().setSupportsChangeAnimations(false);
        getQueueViewAdapter().notifyItemRangeChanged(0, queueTracks.size());
    }

    private int getTimeToPlay(int itemIndex, int currentTrackRemaining, List<Track> tracks){
        if (itemIndex >= queue.size()) return 0;
        int currentTimeToFinish = currentTrackRemaining;
        for (int i = 0; i < itemIndex; i++){
            Track track = tracks.get(i);
            currentTimeToFinish += track.getDurationMs();
        }
        return currentTimeToFinish;
    }

    @SuppressLint("DefaultLocale")
    private String getTimeToPlayString(int millis){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        if (minutes > 0) return String.format("in %02dm", minutes);
        return String.format("in %02ds", TimeUnit.MILLISECONDS.toSeconds(millis));
    }

    public RecyclerView getQueueView() {
        return queueView;
    }

    public QueueAdapter getQueueViewAdapter(){
        return (QueueAdapter) queueView.getAdapter();
    }

    public DefaultItemAnimator getQueueViewAnimator(){
        return (DefaultItemAnimator) queueView.getItemAnimator();
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

    public void setOnPreviousClickedListener(OnClickListener previousClicked) {
        this.previousClicked = previousClicked;
    }

    public void setOnShuffleDeactivateClickedListener(OnClickListener shuffleDeactivateClicked) {
        this.shuffleDeactivateClicked = shuffleDeactivateClicked;
    }

    public void setOnShuffleActivateClickedListener(OnClickListener shuffleActivateClicked) {
        this.shuffleActivateClicked = shuffleActivateClicked;
    }

    public void setOnRepeatDeactivateClickedListener(OnClickListener repeatDeactivateClicked) {
        this.repeatDeactivateClicked = repeatDeactivateClicked;
    }

    public void setOnRepeatActivateClickedListener(OnClickListener repeatActivateClicked) {
        this.repeatActivateClicked = repeatActivateClicked;
    }

    public void setOnVolumeChangedListener(OnVolumeChangedListener volumeChanged) {
        this.volumeChanged = volumeChanged;
    }

    private void onPlayClicked(View v) {
        if (active)
            if (playing && pauseClicked != null) {
                pauseClicked.onClick(v);
                setPlaying(false);
                notifyDataChanged();
            } else if (!playing && playClicked != null) {
                playClicked.onClick(v);
                setPlaying(true);
                notifyDataChanged();
            }
    }

    private void onShuffleClicked(View v) {
        if (active)
            if (shuffle && shuffleDeactivateClicked != null) {
                shuffleDeactivateClicked.onClick(v);
                setShuffle(false);
                notifyDataChanged();
            } else if (!shuffle && shuffleActivateClicked != null) {
                shuffleActivateClicked.onClick(v);
                setShuffle(true);
                notifyDataChanged();
            }
    }

    private void onRepeatingClicked(View v) {
        if (active)
            if (repeating && repeatDeactivateClicked != null) {
                repeatDeactivateClicked.onClick(v);
                setRepeating(false);
                notifyDataChanged();
            } else if (!repeating && repeatActivateClicked != null) {
                repeatActivateClicked.onClick(v);
                setRepeating(true);
                notifyDataChanged();
            }
    }

    private void onSkipClicked(View v) {
        if (active && skipClicked != null)
            skipClicked.onClick(v);
    }

    private void onPreviousClicked(View v) {
        if (active && previousClicked != null)
            previousClicked.onClick(v);
    }

    private void onVolumeChanged(SeekBar seekBar, int percent) {
        if (active && volumeChanged != null)
            volumeChanged.onChanged(seekBar, percent);
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
        trackNameView.setText(active && trackName != null ? trackName : "No player found");
        artistAlbumNameView.setText(active && artistName != null ? albumName != null ? artistName + " - " + albumName : artistName : "");
        playButton.setImageDrawable(active ? playing ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        skipButton.setImageDrawable(active ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_skip_next, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        previousButton.setImageDrawable(active ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_skip_previous, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        shuffleButton.setImageDrawable(active ? shuffle ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shuffle_on, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_shuffle_off, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        repeatButton.setImageDrawable(active ? repeating ?
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_repeat_on, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_repeat_off, null) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unavailable, null));
        volumeIndicator.setProgress(volume, true);
        progressIndicator.setProgress((int) ((progress / (float) duration) * 100f), true);
        progressView.setText(toTime(progress));
        durationView.setText(toTime(duration));
        if (active && albumImage != null)
            albumImageView.setImageBitmap(albumImage);
        else
            albumImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_logo_corporate, null));
    }

    @SuppressLint("DefaultLocale")
    public String toTime(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public interface OnVolumeChangedListener extends SeekBar.OnSeekBarChangeListener {
        AtomicInteger percent = new AtomicInteger(0);

        @Override
        default void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            percent.set(i);
        }

        @Override
        default void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        default void onStopTrackingTouch(SeekBar seekBar) {
            onChanged(seekBar, percent.get());
        }

        void onChanged(SeekBar seekBar, int percent);
    }

    public static class QueueAdapter extends RecyclerView.Adapter<QueueViewHolder> {

        private final List<QueueTrack> tracks;

        private QueueAdapter(List<QueueTrack> tracks) {
            this.tracks = tracks;
            super.setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            String sid = tracks.get(position).track.getId();
            return UUID.nameUUIDFromBytes(sid.getBytes()).getMostSignificantBits();
        }

        @NonNull
        @Override
        public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefab_queue_item, parent, false);
            return new QueueViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
            QueueTrack track = tracks.get(position);
            if (track == null) return;
            holder.trackName.setText(track.track.getName());
            holder.artistView.setText(track.track.getArtists()[0].getName());
            holder.timeToPlay.setText(track.ttp);
            holder.imageView.setImageBitmap(track.image);
        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }
    }

    public static class QueueViewHolder extends RecyclerView.ViewHolder{

        private final TextView trackName;
        private final TextView artistView;
        private final TextView timeToPlay;
        private final ImageView imageView;

        public QueueViewHolder(View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.trackName);
            artistView = itemView.findViewById(R.id.artistName);
            timeToPlay = itemView.findViewById(R.id.time_to_play);
            imageView = itemView.findViewById(R.id.albumImage);
        }
    }

    public static class QueueTrack{

        @NonNull
        public final Track track;
        @NonNull
        public String ttp;
        @NonNull
        public Bitmap image;

        public QueueTrack(@NonNull Track track, @NonNull String ttp, @NonNull Bitmap bitmap) {
            this.track = track;
            this.ttp = ttp;
            this.image = bitmap;
        }

        public void setTtp(@NonNull String ttp) {
            this.ttp = ttp;
        }

        public void setImage(@NonNull Bitmap image){
            this.image = image;
        }
    }
}