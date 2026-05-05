package com.pl.donauturm.spotify;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.spotify.databinding.PrefabSearchItemPlaylistBinding;
import com.pl.donauturm.spotify.databinding.PrefabSearchItemPlaylistTrackBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class SearchAdapterPlaylist extends SearchAdapter<SearchAdapterPlaylist.ImagePlaylist, PrefabSearchItemPlaylistBinding>{

    public SearchAdapterPlaylist(RecyclerView recyclerView, List<ImagePlaylist> tracks) {
        super(recyclerView, tracks);
    }

    public SearchAdapterPlaylist(RecyclerView mRecyclerView, List<ImagePlaylist> tracks, String account1, String account2) {
        super(mRecyclerView, tracks, account1, account2);
    }

    @Override
    protected PrefabSearchItemPlaylistBinding getLayoutBinding(@NonNull ViewGroup parent, LayoutInflater inflater) {
        return PrefabSearchItemPlaylistBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(@NonNull ViewHolder<PrefabSearchItemPlaylistBinding> holder, PrefabSearchItemPlaylistBinding binding, ImagePlaylist imagePlaylist, int position) {
        // setting data
//        holder.binding.account1.setText(getAccount1());
//        holder.binding.account2.setText(getAccount2());
//        holder.binding.buttonLeft.setCardBackgroundColor(isQueueable(getAccount1()) ? getColor(R.color.teal_50) : getColor(R.color.gray_200));
//        holder.binding.buttonRight.setCardBackgroundColor(isQueueable(getAccount2()) ? getColor(R.color.teal_50) : getColor(R.color.gray_200));
//        holder.binding.play1.setImageDrawable(isQueueable(getAccount1()) ? getDrawable(R.drawable.ic_play) : getDrawable(R.drawable.ic_unavailable));
//        holder.binding.play2.setImageDrawable(isQueueable(getAccount2()) ? getDrawable(R.drawable.ic_play) : getDrawable(R.drawable.ic_unavailable));
        binding.playlistName.setText(imagePlaylist.getPlaylist().getName());
        binding.creatorName.setText(String.valueOf(imagePlaylist.getPlaylist().getOwner().getDisplayName()));
        binding.trackCount.setText(imagePlaylist.getPlaylist().getTracks().getTotal() + " tracks");
        Bitmap bitmap = imagePlaylist.getBitmap();
        if (bitmap != null) binding.playlistImage.setImageBitmap(bitmap);
        else binding.playlistImage.setImageDrawable(getDrawable(R.drawable.img_logo_corporate));

        //recycler
        binding.playlistTracks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        PlaylistTrackAdapter nestedAdapter = new PlaylistTrackAdapter(binding.playlistTracks, new ArrayList<>(List.of(new ImagePlaylist.PlaceHolder())));
        nestedAdapter.setQueueableCheck(queueableCheck);
        nestedAdapter.addOnActionListener(onActionListeners.toArray(new OnActionListener[]{}));
        binding.playlistTracks.setAdapter(nestedAdapter);

        //buttons
//        holder.binding.buttonLeft.setOnClickListener(v -> notifyQueue(imagePlaylist.getTrack(), getAccount1()));
//        holder.binding.buttonRight.setOnClickListener(v -> notifyQueue(imagePlaylist.getTrack(), getAccount2()));
    }

    @Nullable
    @Override
    protected View getExpandableView(PrefabSearchItemPlaylistBinding binding) {
        return binding.expContent;
    }

    @Override
    protected void onExpand(@NonNull ViewHolder<PrefabSearchItemPlaylistBinding> holder) {
        ImagePlaylist imagePlaylist = getItemAt(holder.getAdapterPosition());
        imagePlaylist.loadTracks(getApi()).whenComplete((tracks, throwable) -> onPlaylistTracksLoaded(holder, tracks));
    }

    private void onPlaylistTracksLoaded(@NonNull ViewHolder<PrefabSearchItemPlaylistBinding> holder, List<Track> tracks) {
        RecyclerView.Adapter<?> adapt = holder.binding.playlistTracks.getAdapter();
        PlaylistTrackAdapter adapter = getCustomAdapter(adapt, PlaylistTrackAdapter.class);
        if (adapter == null || adapter.getItemCount() == tracks.size()) return;
        List<SearchAdapterTrack.ImageTrack> imageTracks = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            SearchAdapterTrack.ImageTrack imageTrack = new SearchAdapterTrack.ImageTrack(track);
            imageTrack.loadBitmap((bitmap, item, data) -> adapter.updateItem(item, data), imageTrack, i);
            imageTracks.add(imageTrack);
        }
        adapter.clear();
        adapter.addItems(imageTracks);
    }

    @Override
    protected boolean requiresApi() {
        return true;
    }

    public static class ImagePlaylist implements BitmapLoadable<ImagePlaylist, Integer>{
        private final PlaylistSimplified playlist;
        private final List<Track> tracks = new ArrayList<>();
        private Bitmap bitmap;

        public ImagePlaylist(PlaylistSimplified playlist) {
            this.playlist = playlist;
        }

        public PlaylistSimplified getPlaylist() {
            return playlist;
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
            return playlist.getImages()[0].getUrl();
        }

        public CompletableFuture<List<Track>> loadTracks(SpotifyApi api){
            CompletableFuture<List<Track>> callback = new CompletableFuture<>();
            new Thread(() -> {
                collectTrackIds(api);
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.complete(tracks);
                });
            }).start();
            return callback;
        }

        public List<Track> getTracks() {
            return tracks;
        }

        private void collectTrackIds(SpotifyApi api) {
            if (!tracks.isEmpty()) return;
            for (int i = 0; i < playlist.getTracks().getTotal(); i += 100){
                PlaylistTrack[] newItems = api.getPlaylistsItems(playlist.getId()).limit(100).offset(i).build().executeAsync().join().getItems();
                Arrays.stream(newItems)
                        .filter(pt -> pt.getTrack() instanceof Track)
                        .map(pt -> ((Track) pt.getTrack()))
                        .forEachOrdered(tracks::add);
            }
        }

        public static class PlaceHolder extends SearchAdapterTrack.ImageTrack{

            static Track.Builder builder;

            static {
                builder = new Track.Builder();
                builder.setName("Loading...");
                builder.setArtists(new ArtistSimplified.Builder().setName("").build());
                builder.setAlbum(new AlbumSimplified.Builder().setName("").build());
                builder.setDurationMs(0);
            }

            public PlaceHolder() {
                super(builder.build());
            }
        }
    }

    public static class PlaylistTrackAdapter extends SearchAdapter<SearchAdapterTrack.ImageTrack, PrefabSearchItemPlaylistTrackBinding>{

        public PlaylistTrackAdapter(RecyclerView recyclerView, List<SearchAdapterTrack.ImageTrack> imageTracks) {
            super(recyclerView, imageTracks);
        }

        public PlaylistTrackAdapter(RecyclerView mRecyclerView, List<SearchAdapterTrack.ImageTrack> imageTracks, String account1, String account2) {
            super(mRecyclerView, imageTracks, account1, account2);
        }

        @Override
        protected PrefabSearchItemPlaylistTrackBinding getLayoutBinding(@NonNull ViewGroup parent, LayoutInflater inflater) {
            return PrefabSearchItemPlaylistTrackBinding.inflate(inflater, parent, false);
        }

        @Override
        protected void setData(@NonNull ViewHolder<PrefabSearchItemPlaylistTrackBinding> holder, PrefabSearchItemPlaylistTrackBinding binding, SearchAdapterTrack.ImageTrack track, int position) {
            // setting data
            binding.account1.setText(getAccount1());
            binding.account2.setText(getAccount2());
            binding.buttonLeft.setCardBackgroundColor(isQueueable(getAccount1()) ? getColor(R.color.teal_50) : getColor(R.color.gray_200));
            binding.buttonRight.setCardBackgroundColor(isQueueable(getAccount2()) ? getColor(R.color.teal_50) : getColor(R.color.gray_200));
            binding.play1.setImageDrawable(isQueueable(getAccount1()) ? getDrawable(R.drawable.ic_play) : getDrawable(R.drawable.ic_unavailable));
            binding.play2.setImageDrawable(isQueueable(getAccount2()) ? getDrawable(R.drawable.ic_play) : getDrawable(R.drawable.ic_unavailable));
            binding.trackName2.setText(track.getTrack().getName());
            binding.artistName2.setText(String.valueOf(track.getTrack().getArtists()[0].getName()));
            binding.albumName2.setText(track.getTrack().getAlbum().getName());
//            holder.binding.duration2.setText(toTime(track.getTrack().getDurationMs()));
            Bitmap bitmap = track.getBitmap();
            if (bitmap != null) binding.imageView3.setImageBitmap(bitmap);
            else binding.imageView3.setImageDrawable(getDrawable(R.drawable.img_logo_corporate));

            //buttons
            holder.binding.buttonLeft.setOnClickListener(v -> notifyQueue(track.getTrack(), getAccount1()));
            holder.binding.buttonRight.setOnClickListener(v -> notifyQueue(track.getTrack(), getAccount2()));
        }

        @Nullable
        @Override
        protected View getExpandableView(PrefabSearchItemPlaylistTrackBinding binding) {
            return binding.expContent;
        }

        @Override
        protected int getTopMostMargin() {
            return 0;
        }

        @Override
        protected int getBottomMostMargin() {
            return 0;
        }
    }
}
