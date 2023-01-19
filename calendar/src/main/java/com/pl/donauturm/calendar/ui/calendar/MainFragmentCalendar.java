package com.pl.donauturm.calendar.ui.calendar;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.calendar.CalendarCredentials;
import com.pl.donauturm.calendar.CalenderDownloader;
import com.pl.donauturm.calendar.databinding.FragmentCalendarBinding;
import com.pl.donauturm.calendar.view.SetEditCalendarCredDialog;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainFragmentCalendar extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarCredentials credentials;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        credentials = CalendarCredentials.getCredentials(requireContext());
        if (credentials == null) {
            SetEditCalendarCredDialog.newInstance(requireContext()).setOnSavedListener((u, p) -> {
                CalendarCredentials creds = new CalendarCredentials(u, p);
                creds.save(requireContext());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> onViewCreated(view, savedInstanceState));
            }).show(getParentFragmentManager(), "SetEditDMCDialog");
            return;
        }
        this.binding.calendarRecycler.setAdapter(new CalendarListAdapter());
        this.binding.calendarRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        this.binding.refresh.setEnabled(true);
        this.binding.refresh.setRefreshing(true);
        this.binding.refresh.setOnRefreshListener(this::onRefresh);
        this.onRefresh();
    }

    private void onRefresh() {
        this.getVEvent(this::calendarLoadComplete);
    }

    private void calendarLoadComplete(@Nullable List<VEvent> events){
        if (events == null || events.isEmpty()){
            return;
        }
        List<VEvent> sortedEvents = events.stream().sorted(Comparator.comparing(VEvent::getStartDate)).collect(Collectors.toList());
        Date now = Calendar.getInstance().getTime();
        VEvent fistAhead = sortedEvents.stream().filter(ve -> ve.getStartDate().getDate().compareTo(now) > 0).findFirst().orElseGet(() -> sortedEvents.get(sortedEvents.size()-1));
        new Handler(Looper.getMainLooper()).post(() -> {
            this.binding.refresh.setRefreshing(false);
            this.binding.refresh.setEnabled(false);
            RecyclerView.Adapter<?> adapter = binding.calendarRecycler.getAdapter();
            if (adapter instanceof CalendarListAdapter) {
                ((CalendarListAdapter) adapter).setEvents(sortedEvents);
                ((CalendarListAdapter) adapter).setCurrentEvent(fistAhead);
            }
        });
    }

    private void getVEvent(CalenderDownloader.VEventProvider callback) {
        new CalenderDownloader().tryGetAllEvents(credentials.getUsername(), credentials.getPassword(), callback);
    }
}
