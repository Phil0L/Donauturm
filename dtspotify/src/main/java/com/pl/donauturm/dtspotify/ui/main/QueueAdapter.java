package com.pl.donauturm.dtspotify.ui.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.commons.util.ThemeManager;
import com.pl.donauturm.dtspotify.DTSpotify;
import com.pl.donauturm.dtspotify.commons.DurationFormatter;
import com.pl.donauturm.dtspotify.databinding.PrefabTrackQueueBinding;
import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> implements ThemeManager.OnThemSwitch {

    private static final String DELAY_UPDATE = "delayUpdate";
    private static final String THEME_UPDATE = "themeUpdate";

    private Context context;
    private final List<QueuedTrack> localDataSet;

    /**
     * Initialize the dataset of the Adapter
     * by RecyclerView
     */
    public QueueAdapter() {
        localDataSet = new ArrayList<>();
        ThemeManager.getInstance().addThemeChangeListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateQueueItems(List<Track> newData) {
        localDataSet.clear();
        localDataSet.addAll(newData.stream().map(QueuedTrack::new).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    public void updateQueueDelay(int currentTrackProgress){
        long delay = currentTrackProgress;
        for (QueuedTrack queuedTrack : localDataSet) {
            queuedTrack.setDelay(delay);
            delay += queuedTrack.track.getDurationMs();
        }
        notifyItemRangeChanged(0, getItemCount(), DELAY_UPDATE);
    }

    public void updateQueueTheme(){
        notifyItemRangeChanged(0, getItemCount(), THEME_UPDATE);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        PrefabTrackQueueBinding binding = PrefabTrackQueueBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (localDataSet.get(position) == null) return;
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        QueuedTrack data = localDataSet.get(position);
        viewHolder.getTitleView().setText(data.getName());
        viewHolder.getArtistView().setText(data.getArtist());
        viewHolder.getDelayView().setText(data.getDelay());
        viewHolder.getCardView().setCardBackgroundColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.colorBackground));
        viewHolder.getTitleView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
        viewHolder.getArtistView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
        viewHolder.getDelayView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
        if (!data.getAlbumUrl().equals(""))
            new BitmapDownloadRequest(data.getAlbumUrl()).requestAsync(bitmap ->
                    new Handler(Looper.getMainLooper()).post(() ->
                            viewHolder.getImageView().setImageBitmap(bitmap)));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        if (localDataSet.get(position) == null) return;
        if (payloads.isEmpty() || !payloads.contains(DELAY_UPDATE) || !payloads.contains(THEME_UPDATE))
            onBindViewHolder(viewHolder, position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (payloads.contains(DELAY_UPDATE)){
            QueuedTrack data = localDataSet.get(position);
            viewHolder.getDelayView().setText(data.getDelay());
        }

        if (payloads.contains(THEME_UPDATE)){
            viewHolder.getCardView().setCardBackgroundColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.colorBackground));
            viewHolder.getTitleView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
            viewHolder.getArtistView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
            viewHolder.getDelayView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @Override
    public void onThemeSwitch(ThemeManager.Theme theme) {
        if (DTSpotify.inDayNightReload) return;
        updateQueueTheme();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PrefabTrackQueueBinding binding;

        public ViewHolder(@NonNull PrefabTrackQueueBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTitleView() {
            return binding.trackTitle;
        }

        public TextView getArtistView() {
            return binding.trackArtist;
        }

        public ImageView getImageView() {
            return binding.playingImage;
        }

        public TextView getDelayView() {
            return binding.duration;
        }

        public CardView getCardView(){
            return binding.playingCard;
        }
    }

    public static class QueuedTrack{

        @NonNull
        private final Track track;
        private long delay;

        public QueuedTrack(@NonNull Track track) {
            this.track = track;
            this.delay = 0L;
        }

        void setDelay(long delay){
            this.delay = delay;
        }

        public String getName(){
            return track.getName() != null ? track.getName() : "";
        }

        public String getArtist(){
            if (track.getArtists() != null && track.getArtists().length >= 1 && track.getArtists()[0] != null && track.getArtists()[0].getName() != null)
                return track.getArtists()[0].getName();
            return "";
        }

        public String getAlbumUrl(){
            if (track.getAlbum() != null && track.getAlbum().getImages() != null && track.getAlbum().getImages().length >= 1 && track.getAlbum().getImages()[0].getUrl() != null)
                return track.getAlbum().getImages()[0].getUrl();
            return "";
        }

        public String getDelay(){
            return DurationFormatter.dynamic(delay, "in %s");
        }
    }
}

