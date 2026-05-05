package com.pl.donauturm.dtspotify.ui.main;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.dtspotify.R;
import com.pl.donauturm.dtspotify.commons.lyrics.Lyrics;
import com.pl.donauturm.dtspotify.databinding.PrefabItemLyricsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder> {

    private static final String TIME_UPDATE = "timeUpdate";
    private static final int FUTURE_OFFSET = 500; // ms
    private Resources resources;
    private final List<LyricsLine> localDataSet;
    private int presentIndex = 0;

    /**
     * Initialize the dataset of the Adapter
     * by RecyclerView
     */
    public LyricsAdapter() {
        localDataSet = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLyricsContent(@NonNull Lyrics newData) {
        localDataSet.clear();
        localDataSet.addAll(newData.lines().stream().map(LyricsLine::new).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    public void updateProgress(long progress){
        boolean inFuture = false;
        for (int i = 0, j = 0; i < getItemCount(); i++) {
            LyricsLine line = localDataSet.get(i);
            LyricsLine lineBefore = localDataSet.get(j);
            line.setProgress(progress);
            line.setPresent(false);
            if (line.isFuture() && !inFuture) {
                lineBefore.setPresent(true);
                inFuture = true;
                presentIndex = j;
            }
            if (i != 0) j++;
        }
        notifyItemRangeChanged(0, getItemCount(), TIME_UPDATE);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        PrefabItemLyricsBinding binding = PrefabItemLyricsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        resources = viewGroup.getResources();
        return new ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (localDataSet.get(position) == null) return;
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        LyricsLine data = localDataSet.get(position);
        viewHolder.getTextView().setText(data.getWords());
        viewHolder.getTextView().setTextColor(ResourcesCompat.getColor(resources, data.getTextColor(), null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        if (localDataSet.get(position) == null) return;
        if (payloads.isEmpty() || !payloads.contains(TIME_UPDATE))
            onBindViewHolder(viewHolder, position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        LyricsLine data = localDataSet.get(position);
        if (payloads.contains(TIME_UPDATE)){
            viewHolder.getTextView().setTextColor(ResourcesCompat.getColor(resources, data.getTextColor(), null));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public int getPresentIndex(){
        return presentIndex;
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PrefabItemLyricsBinding binding;

        public ViewHolder(@NonNull PrefabItemLyricsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTextView() {
            return binding.lyricsBlueprintFuture;
        }
    }

    public static class LyricsLine {
        private final Lyrics.Line line;
        private long progress;
        private boolean present;

        public LyricsLine(Lyrics.Line line) {
            this.line = line;
            this.progress = 0;
        }

        public String getTimeTag() {
            return line.getTimeTag();
        }

        public long getTimeMillis(){
            return line.getTimeMillis();
        }

        public String getWords() {
            return line.getWords();
        }

        public long getProgress() {
            return progress;
        }

        public boolean isPresent(){
            return present;
        }

        public boolean isPast(){
            return getTimeMillis() < getProgress() + FUTURE_OFFSET;
        }

        public boolean isFuture(){
            return getTimeMillis() > getProgress() + FUTURE_OFFSET;
        }

        @ColorRes
        public int getTextColor(){
            if (isPresent()) return R.color.pink_600;
            if (isFuture()) return R.color.gray_700;
            if (isPast()) return R.color.teal_700;
            return R.color.gray_900;
        }

        public void setProgress(long progress) {
            this.progress = progress;
        }

        public void setPresent(boolean bool){
            this.present = bool;
        }
    }


}

