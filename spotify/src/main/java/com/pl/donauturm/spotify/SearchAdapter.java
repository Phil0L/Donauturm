package com.pl.donauturm.spotify;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.pl.donauturm.spotify.databinding.PrefabSearchTypeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;

public abstract class SearchAdapter<ITEM, BIND extends ViewBinding> extends RecyclerView.Adapter<SearchAdapter.ViewHolder<BIND>> {

    private final RecyclerView mRecyclerView;
    private final String account1;
    private final String account2;
    private final List<ITEM> items;
    protected final List<OnActionListener> onActionListeners;
    protected final List<OnItemClickListener<BIND>> onClickListeners;
    protected Queueable queueableCheck = (t, i, s) -> false;
    private int expandedPosition = -1;
    private SpotifyApi api;


    public SearchAdapter(RecyclerView recyclerView, List<ITEM> items) {
        this.mRecyclerView = recyclerView;
        this.account1 = "spotify1@donauturm.eu";
        this.account2 = "spotify2@donauturm.eu";
        this.onActionListeners = new ArrayList<>();
        this.onClickListeners = new ArrayList<>();
        this.items = items;
    }

    public SearchAdapter(RecyclerView mRecyclerView, List<ITEM> items, String account1, String account2) {
        this.mRecyclerView = mRecyclerView;
        this.account1 = account1;
        this.account2 = account2;
        this.onActionListeners = new ArrayList<>();
        this.onClickListeners = new ArrayList<>();
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder<BIND> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BIND bind = getLayoutBinding(parent, LayoutInflater.from(parent.getContext()));
        return new ViewHolder<>(bind);
    }

    protected abstract BIND getLayoutBinding(@NonNull ViewGroup parent, LayoutInflater inflater);

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<BIND> holder, int position) {
        // top most and bottom most card
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.binding.getRoot().getLayoutParams();
        if (position == 0) lp.topMargin = getDp(getTopMostMargin(), holder);
        else lp.topMargin = getDp(0, holder);
        if (position == getItemCount() - 1) lp.bottomMargin = getDp(getBottomMostMargin(), holder);
        else lp.bottomMargin = getDp(0, holder);
        holder.binding.getRoot().setLayoutParams(lp);

        // expanding and collapsing
        View expandableView = getExpandableView(holder.binding);
        if (position == expandedPosition && expandableView != null)
            expandableView.setVisibility(View.VISIBLE);
        else if (expandableView != null)
            expandableView.setVisibility(View.GONE);
        holder.binding.getRoot().setOnClickListener(v -> onItemClicked(v, holder, position));

        // parent attributes
        setData(holder, holder.binding, items.get(position), position);
    }

    protected void onItemClicked(View v, @NonNull ViewHolder<BIND> holder, int position) {
        notifyClick(v, holder, position);
        View expandableView = getExpandableView(holder.binding);
        if (expandableView == null) return;
        if (expandableView.getVisibility() == View.VISIBLE)
            shrink(holder);
        else
            expand(holder);
        notifyItemChanged(holder.getAdapterPosition());
    }

    protected void expand(@NonNull ViewHolder<BIND> holder) {
        View expandableView = getExpandableView(holder.binding);
        if (expandableView == null) return;
        int oldPosition = getExpandedPosition();
        setExpandedPosition(holder.getAdapterPosition());
        if (oldPosition != -1) {
            RecyclerView.ViewHolder oldHolder = getRecyclerView().findViewHolderForAdapterPosition(oldPosition);
            if (oldHolder instanceof ViewHolder) {
                @SuppressWarnings("unchecked")
                ViewHolder<BIND> oldBindHolder = (ViewHolder<BIND>) oldHolder;
                View expandableViewOld = getExpandableView(oldBindHolder.binding);
                if (expandableViewOld != null) {
                    onShrink(oldBindHolder);
                    expandableViewOld.setVisibility(View.GONE);
                }
            }
        }
        onExpand(holder);
        expandableView.setVisibility(View.VISIBLE);
    }

    protected void shrink(@NonNull ViewHolder<BIND> holder) {
        View expandableView = getExpandableView(holder.binding);
        if (expandableView == null) return;
        onShrink(holder);
        setExpandedPosition(-1);
        expandableView.setVisibility(View.GONE);
    }

    protected void onExpand(@NonNull ViewHolder<BIND> holder) {

    }

    protected void onShrink(@NonNull ViewHolder<BIND> holder) {

    }

    protected abstract void setData(@NonNull ViewHolder<BIND> holder, BIND binding, ITEM item, int position);

    protected int getTopMostMargin() {
        return 8;
    }

    protected int getBottomMostMargin() {
        return 80;
    }

    @Nullable
    protected View getExpandableView(BIND bind) {
        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Context getContext() {
        return mRecyclerView.getContext();
    }

    public void setSearchApi(@NonNull SpotifyApi api) {
        this.api = api;
    }

    protected SpotifyApi getApi() {
        return api;
    }

    protected boolean requiresApi() {
        return false;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public String getAccount1() {
        return account1;
    }

    public String getAccount2() {
        return account2;
    }

    public int getExpandedPosition() {
        return expandedPosition;
    }

    public void setExpandedPosition(int expandedPosition) {
        this.expandedPosition = expandedPosition;
    }

    public void addOnActionListener(OnActionListener onActionListener) {
        onActionListeners.add(onActionListener);
    }

    public void addOnActionListener(OnActionListener... onActionListener) {
        onActionListeners.addAll(Arrays.asList(onActionListener));
    }

    public void addOnClickListener(OnItemClickListener<BIND> onClickListener) {
        onClickListeners.add(onClickListener);
    }

    public void setQueueableCheck(Queueable queueable) {
        queueableCheck = queueable;
    }

    public List<ITEM> getItems() {
        return items;
    }

    protected ITEM getItemAt(int index) {
        return getItems().get(index);
    }

    public void addItem(ITEM item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addItems(List<ITEM> item) {
        items.addAll(item);
        notifyItemRangeInserted(items.size() - item.size(), item.size());
    }

    public void updateItem(ITEM item, int position) {
        items.set(position, item);
        notifyItemChanged(position);
    }

    public void clear() {
        setExpandedPosition(-1);
        int count = items.size();
        items.clear();
        notifyItemRangeRemoved(0, count);
    }

    protected boolean isQueueable(String account) {
        if (Objects.equals(account, account1)) {
            return queueableCheck.isQueueable(null, 1, account1);
        } else if (Objects.equals(account, account2)) {
            return queueableCheck.isQueueable(null, 2, account2);
        }
        return false;
    }

    @ColorInt
    public int getColor(@ColorRes int color) {
        return ResourcesCompat.getColor(getRecyclerView().getResources(), color, null);
    }

    public Drawable getDrawable(@DrawableRes int drawable) {
        return ResourcesCompat.getDrawable(getRecyclerView().getResources(), drawable, null);
    }

    protected int getDp(int dp, @NonNull ViewHolder<BIND> holder) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, holder.binding.getRoot().getResources().getDisplayMetrics());
    }

    @SuppressLint("DefaultLocale")
    public String toTime(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    protected void notifyQueue(Track track, String account) {
        for (OnActionListener listener : onActionListeners) {
            if (isQueueable(account))
                listener.onQueueClicked(track, account.equals(account1) ? 1 : 2, account);
        }
    }

    protected void notifyClick(View v, ViewHolder<BIND> viewHolder, int position) {
        for (OnItemClickListener<BIND> listener : onClickListeners) {
            listener.onClick(v, viewHolder, position);
        }
    }

    @Nullable
    protected <I, B extends ViewBinding, RT extends SearchAdapter<I, B>> RT getCustomAdapter(@Nullable RecyclerView.Adapter<?> unknownAdapter, Class<RT> adapterClass) {
        if (unknownAdapter == null) return null;
        if (unknownAdapter.getClass().isAssignableFrom(adapterClass))
            return adapterClass.cast(unknownAdapter);
        return null;
    }

    public static class ViewHolder<BIND extends ViewBinding> extends RecyclerView.ViewHolder {

        public final BIND binding;

        public ViewHolder(@NonNull BIND binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnActionListener {
        void onQueueClicked(Track track, int player, String account);
    }

    public interface OnItemClickListener<BIND extends ViewBinding> {
        void onClick(View v, ViewHolder<BIND> viewHolder, int position);
    }

    public interface Queueable {
        boolean isQueueable(@Nullable Track track, int i, String account);
    }

    public static class SearchTypeAdapter extends RecyclerView.Adapter<SearchTypeAdapter.ViewHolder> {

        private int selected = 0;
        private final List<OnItemClickListener> onClickListeners;

        public SearchTypeAdapter() {
            this.onClickListeners = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return SearchType.values().length;
        }

        @NonNull
        @Override
        public SearchTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PrefabSearchTypeBinding binding = PrefabSearchTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchTypeAdapter.ViewHolder holder, int position) {
            holder.binding.text.setText(SearchType.values()[position].getDisplay());
            holder.binding.getRoot().setOnClickListener(v -> notifyClick(position));
            if (position != selected)
                holder.binding.button.setCardBackgroundColor(Color.TRANSPARENT);
            else
                holder.binding.button.setCardBackgroundColor(ResourcesCompat.getColor(holder.binding.getRoot().getResources(), R.color.teal_10, null));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSelected(int i) {
            selected = i;
        }

        public SearchType getSelected() {
            return SearchType.values()[selected];
        }

        public void addOnItemClickListener(OnItemClickListener clickListener) {
            onClickListeners.add(clickListener);
        }

        protected void notifyClick(int position) {
            if (position == selected) return;
            int oldPosition = selected;
            selected = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
            for (OnItemClickListener itemClickListener : onClickListeners) {
                itemClickListener.onTypeItemChanged(position, SearchType.values()[position]);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final PrefabSearchTypeBinding binding;

            public ViewHolder(PrefabSearchTypeBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        public enum SearchType {
            TRACK("Tracks"),
            PLAYLIST("Playlists"),
            ALBUM("Albums"),
            ARTIST("Artists"),
            USER("User");

            private final String display;

            SearchType(String display) {
                this.display = display;
            }

            public String getDisplay() {
                return display;
            }
        }

        public interface OnItemClickListener {
            void onTypeItemChanged(int index, SearchType type);
        }
    }
}
