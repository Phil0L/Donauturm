package com.pl.donauturm.calendar.ui.calendar;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.calendar.databinding.PrefabBottomExtenderBinding;
import com.pl.donauturm.calendar.databinding.PrefabTopExtenderBinding;
import com.pl.donauturm.calendar.databinding.PrefebVeventBinding;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.List;

public class CalendarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VEvent> events;
    private int topIndex = 0;
    private int bottomIndex = 0;
    private boolean isAtEnd = false;
    private boolean isAtStart = false;

    private static final int DEFAULT_AMOUNT = 10;
    private static final int ADDING_AMOUNT = 3;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 1:
                PrefabTopExtenderBinding tebinding = PrefabTopExtenderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new TopAdderViewHolder(tebinding.getRoot());
            case 2:
                PrefabBottomExtenderBinding bebinding = PrefabBottomExtenderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new BottomAdderViewHolder(bebinding.getRoot());
            default:
                PrefebVeventBinding binding = PrefebVeventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new CalendarListViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CalendarListViewHolder) {
            CalendarListViewHolder clHolder = (CalendarListViewHolder) holder;
            clHolder.setVEvent(events.get(position + topIndex - 1));
        }
        if (holder instanceof BottomAdderViewHolder){
            BottomAdderViewHolder baHolder = (BottomAdderViewHolder) holder;
            baHolder.itemView.setOnClickListener(v -> this.extendBottom());
        }
        if (holder instanceof TopAdderViewHolder){
            TopAdderViewHolder taHolder = (TopAdderViewHolder) holder;
            taHolder.itemView.setOnClickListener(v -> this.extendTop());
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 0 = normal VEvent
        // 1 = Top Extender
        // 2 = Bottom Extender
        if (position == 0 && !isAtStart) return 1;
        if (position == getItemCount()-1 && !isAtEnd) return 2;
        return 0;
    }

    @Override
    public int getItemCount() {
        if (events == null) return 0;
        return getSizeShown() + (isAtStart? 0 : 1) + (isAtEnd ? 0 : 1);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEvents(List<VEvent> list) {
        this.events = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentEvent(VEvent fistAhead) {
        int index = events.indexOf(fistAhead);
        this.topIndex = Math.max(index, 0);
        if (topIndex == 0) this.isAtStart = true;
        this.bottomIndex = Math.min(index + DEFAULT_AMOUNT + 1, events.size()) - 2;
        if (bottomIndex == events.size() - 2) this.isAtEnd = true;
        notifyDataSetChanged();
    }

    private void extendBottom(){
        int preIC = getItemCount();
        bottomIndex = Math.min(bottomIndex + ADDING_AMOUNT, events.size() - 2);
        if (bottomIndex == events.size() - 2){
            // recycler is at the end
            notifyItemRemoved(preIC - 1);
            notifyItemRangeInserted(preIC - 1, getItemCount() - preIC + 1);
            this.isAtEnd = true;
            return;
        }
        notifyItemRemoved(preIC-1);
        notifyItemRangeInserted(preIC-1, 4);
    }

    private void extendTop() {
        int preIC = getItemCount();
        topIndex = Math.max(topIndex - ADDING_AMOUNT, 0);
        if (topIndex == 0){
            // recycler is at the start
            notifyItemRemoved(0);
            notifyItemRangeInserted(0, getItemCount() - preIC + 1);
            this.isAtStart = true;
            return;
        }
        notifyItemRemoved(0);
        notifyItemRangeInserted(0, 4);
    }

    public int getSizeShown(){
        return bottomIndex - topIndex + 1;
    }

    public static class CalendarListViewHolder extends RecyclerView.ViewHolder{

        private final PrefebVeventBinding binding;
        private VEvent event;

        public CalendarListViewHolder(@NonNull PrefebVeventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setVEvent(VEvent event){
            this.event = event;
            this.binding.vevent.setVEvent(event);
        }

    }

    public static class TopAdderViewHolder extends RecyclerView.ViewHolder{

        public TopAdderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class BottomAdderViewHolder extends RecyclerView.ViewHolder{

        public BottomAdderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }



}
