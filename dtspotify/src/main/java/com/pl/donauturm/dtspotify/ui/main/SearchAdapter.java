package com.pl.donauturm.dtspotify.ui.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.commons.util.ThemeManager;
import com.pl.donauturm.dtspotify.DTSpotify;
import com.pl.donauturm.dtspotify.R;
import com.pl.donauturm.dtspotify.commons.AgingList;
import com.pl.donauturm.dtspotify.commons.DurationFormatter;
import com.pl.donauturm.dtspotify.databinding.PrefabTrackSearchBinding;
import com.pl.donauturm.spotify.CallbackList;
import com.pl.donauturm.spotify.customrequests.BitmapDownloadRequest;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements ThemeManager.OnThemSwitch {

    private static final String QUEUE_UPDATE = "queueUpdate";
    private static final String THEME_UPDATE = "themeUpdate";
    private final CallbackList<OnQueueTrack> callbacks;
    private final List<SearchTrack> localDataSet;
    private final AgingList<String> durableQueueIds;
    private final List<String> queueIds;
    private Context context;
    private Resources resources;

    /**
     * Initialize the dataset of the Adapter
     * by RecyclerView
     */
    public SearchAdapter() {
        localDataSet = new ArrayList<>();
        queueIds = new ArrayList<>();
        durableQueueIds = new AgingList<>();
        callbacks = new CallbackList<>();
        ThemeManager.getInstance().addThemeChangeListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSearchItems(@NonNull List<Track> newData) {
        localDataSet.clear();
        localDataSet.addAll(newData.stream().map(SearchTrack::new).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    public void updateQueueTheme(){
        notifyItemRangeChanged(0, getItemCount(), THEME_UPDATE);
    }

    public void provideCurrentQueue(@NonNull List<Track> queue) {
        durableQueueIds.age();
        queueIds.clear();
        queueIds.addAll(queue.stream().map(Track::getId).collect(Collectors.toList()));
        queueIds.addAll(durableQueueIds);
        notifyItemRangeChanged(0, getItemCount(), QUEUE_UPDATE);
    }

    public void onQueueTrackClicked(OnQueueTrack callback) {
        callbacks.clear();
        callbacks.add(callback);
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
        PrefabTrackSearchBinding binding = PrefabTrackSearchBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        resources = viewGroup.getResources();
        return new ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (localDataSet.get(position) == null) return;
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        SearchTrack data = localDataSet.get(position);
        viewHolder.getTitleView().setText(data.getName());
        viewHolder.getArtistView().setText(data.getArtist());
        viewHolder.getDurationView().setText(data.getDuration());
        viewHolder.getButton().setImageDrawable(data.getButtonImage(queueIds, resources));
        viewHolder.getContainer().setCardBackgroundColor(data.getBackgroundColor(queueIds, context));
        viewHolder.getContainer().setOnClickListener(onItemClicked(data.track.getId()));
        viewHolder.getButton().setOnClickListener(onItemClicked(data.track.getId()));
        viewHolder.getCardView().setCardBackgroundColor(data.getBackgroundColor(queueIds, context));
        viewHolder.getTitleView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
        viewHolder.getArtistView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
        viewHolder.getButton().setColorFilter(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary), PorterDuff.Mode.SRC_IN);
        if (!data.getAlbumUrl().equals(""))
            new BitmapDownloadRequest(data.getAlbumUrl()).requestAsync(bitmap ->
                    new Handler(Looper.getMainLooper()).post(() ->
                            viewHolder.getImageView().setImageBitmap(bitmap)));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        if (localDataSet.get(position) == null) return;
        if (payloads.isEmpty() || !payloads.contains(QUEUE_UPDATE) || !payloads.contains(THEME_UPDATE))
            onBindViewHolder(viewHolder, position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (payloads.contains(QUEUE_UPDATE)) {
            SearchTrack data = localDataSet.get(position);
            viewHolder.getButton().setImageDrawable(data.getButtonImage(queueIds, resources));
            viewHolder.getContainer().setCardBackgroundColor(data.getBackgroundColor(queueIds, context));
        }

        if (payloads.contains(THEME_UPDATE)){
            SearchTrack data = localDataSet.get(position);
            viewHolder.getCardView().setCardBackgroundColor(data.getBackgroundColor(queueIds, context));
            viewHolder.getTitleView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
            viewHolder.getArtistView().setTextColor(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary));
            viewHolder.getButton().setColorFilter(ThemeManager.ThemeColor.getColor(context, android.R.attr.textColorTertiary), PorterDuff.Mode.SRC_IN);
        }
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    private OnItemClickListener<String> onItemClicked(String id) {
        return new OnItemClickListener<>(id) {
            void onClick(@NonNull String id) {
                if (queueIds.contains(id))
                    return;
                durableQueueIds.add(id, 10);
                queueIds.add(id);
                callbacks.fire(c -> c.queueTrack(id));
                notifyItemRangeChanged(0, getItemCount(), QUEUE_UPDATE);
            }
        };
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
        private final PrefabTrackSearchBinding binding;

        public ViewHolder(@NonNull PrefabTrackSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTitleView() {
            return binding.playingTitle;
        }

        public TextView getArtistView() {
            return binding.playingArtist;
        }

        public ImageView getImageView() {
            return binding.playingImage;
        }

        public TextView getDurationView() {
            return binding.duration;
        }

        public ImageView getButton() {
            return binding.addButton;
        }

        public CardView getContainer() {
            return binding.playingCard;
        }

        public CardView getCardView(){
            return binding.playingCard;
        }
    }

    public static class SearchTrack {

        @NonNull
        private final Track track;

        public SearchTrack(@NonNull Track track) {
            this.track = track;
        }

        public String getName() {
            return track.getName() != null ? track.getName() : "";
        }

        public String getArtist() {
            if (track.getArtists() != null && track.getArtists().length >= 1 && track.getArtists()[0] != null && track.getArtists()[0].getName() != null)
                return track.getArtists()[0].getName();
            return "";
        }

        public String getAlbumUrl() {
            if (track.getAlbum() != null && track.getAlbum().getImages() != null && track.getAlbum().getImages().length >= 1 && track.getAlbum().getImages()[0].getUrl() != null)
                return track.getAlbum().getImages()[0].getUrl();
            return "";
        }

        public String getDuration() {
            return track.getDurationMs() != null ? DurationFormatter.minSec(track.getDurationMs(), "%02d:%02d") : "";
        }

        public boolean isQueued(List<String> queue) {
            return track.getId() != null && queue.contains(track.getId());
        }

        public Drawable getButtonImage(List<String> queue, Resources resources) {
            if (isQueued(queue))
                return ResourcesCompat.getDrawable(resources, R.drawable.ic_check, null);
            else
                return ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null);
        }

        @ColorInt
        public int getBackgroundColor(List<String> queue, Context context) {
            if (isQueued(queue))
                return ThemeManager.ThemeColor.getColor(context, ThemeManager.getInstance().getCurrentTheme(), R.color.teal_200, R.color.teal_700);
            else
                return ThemeManager.ThemeColor.getColor(context, android.R.attr.colorBackground);
        }

    }

    public abstract static class OnItemClickListener<T> implements View.OnClickListener {

        @NonNull
        private final T data;

        protected OnItemClickListener(@NonNull T data) {
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            onClick(data);
        }

        abstract void onClick(@NonNull T data);
    }

    public interface OnQueueTrack {
        void queueTrack(@NonNull String trackId);
    }
}

