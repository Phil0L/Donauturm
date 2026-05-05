package com.pl.donauturm.spotify;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.spotify.databinding.PrefabSearchItemTrackBinding;

import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.Track;

public class SearchAdapterTrack extends SearchAdapter<SearchAdapterTrack.ImageTrack, PrefabSearchItemTrackBinding>{

    public SearchAdapterTrack(RecyclerView recyclerView, List<ImageTrack> tracks) {
        super(recyclerView, tracks);
    }

    public SearchAdapterTrack(RecyclerView mRecyclerView, List<ImageTrack> tracks, String account1, String account2) {
        super(mRecyclerView, tracks, account1, account2);
    }

    @Override
    protected PrefabSearchItemTrackBinding getLayoutBinding(@NonNull ViewGroup parent, LayoutInflater inflater) {
        return PrefabSearchItemTrackBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void setData(@NonNull ViewHolder<PrefabSearchItemTrackBinding> holder, PrefabSearchItemTrackBinding binding, ImageTrack track, int position) {
        // setting data
        holder.binding.account1.setText(getAccount1());
        holder.binding.account2.setText(getAccount2());
        holder.binding.buttonLeft.setCardBackgroundColor(isQueueable(getAccount1()) ? getColor(R.color.teal_50) : getColor(R.color.gray_200));
        holder.binding.buttonRight.setCardBackgroundColor(isQueueable(getAccount2()) ? getColor(R.color.teal_50) : getColor(R.color.gray_200));
        holder.binding.play1.setImageDrawable(isQueueable(getAccount1()) ? getDrawable(R.drawable.ic_play) : getDrawable(R.drawable.ic_unavailable));
        holder.binding.play2.setImageDrawable(isQueueable(getAccount2()) ? getDrawable(R.drawable.ic_play) : getDrawable(R.drawable.ic_unavailable));
        holder.binding.trackName.setText(track.getTrack().getName());
        holder.binding.artistName.setText(String.valueOf(track.getTrack().getArtists()[0].getName()));
        holder.binding.albumName.setText(track.getTrack().getAlbum().getName());
        holder.binding.duration.setText(toTime(track.getTrack().getDurationMs()));
        Bitmap bitmap = track.getBitmap();
        if (bitmap != null) holder.binding.albumImage.setImageBitmap(bitmap);
        else holder.binding.albumImage.setImageDrawable(getDrawable(R.drawable.img_logo_corporate));

        //buttons
        holder.binding.buttonLeft.setOnClickListener(v -> notifyQueue(track.getTrack(), getAccount1()));
        holder.binding.buttonRight.setOnClickListener(v -> notifyQueue(track.getTrack(), getAccount2()));
    }

    @Nullable
    @Override
    protected View getExpandableView(PrefabSearchItemTrackBinding binding) {
        return binding.expContent;
    }

    public static class ImageTrack implements BitmapLoadable<ImageTrack, Integer>{
        private final Track track;
        private Bitmap bitmap;

        public ImageTrack(Track track) {
            this.track = track;
        }

        public Track getTrack() {
            return track;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public String getBitmapUrl() {
            return track.getAlbum().getImages()[0].getUrl();
        }
    }
}
