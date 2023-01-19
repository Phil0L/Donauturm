package com.pl.donauturm.calendar;

import androidx.annotation.NonNull;

import com.pl.donauturm.calendar.api.CalendarRequest;
import com.pl.donauturm.calendar.api.ConnectionManager;
import com.pl.donauturm.calendar.customizer.CustomCalendarBuilder;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CalenderDownloader {

    public List<VEvent> getAllVEvents(InputStream input){
        try {
            CustomCalendarBuilder builder = new CustomCalendarBuilder();
            Calendar calendar = builder.build(input);
            return calendar.getComponents().stream().filter(c -> c instanceof VEvent).map(c -> (VEvent) c).collect(Collectors.toList());
        } catch (IOException | ParserException e) {
            return new ArrayList<>();
        }
    }

    public List<VEvent> getAllVEvents(Reader input){
        try {
            CustomCalendarBuilder builder = new CustomCalendarBuilder();
            Calendar calendar = builder.build(input);
            return calendar.getComponents().stream().filter(c -> c instanceof VEvent).map(c -> (VEvent) c).collect(Collectors.toList());
        } catch (IOException | ParserException e) {
            return new ArrayList<>();
        }
    }

    public List<VEvent> getAllVEvents(String username, String password){
        new ConnectionManager();
        String data = new CalendarRequest(username, password).request();
        return getAllVEvents(new StringReader(data));
    }

    public List<VEvent> getAllAheadVEvents(String username, String password, Date now){
        List<VEvent> vEvents = getAllVEvents(username, password);
        return vEvents.stream()
                .filter(ve -> ve.getStartDate().getDate().compareTo(now) > 0)
                .sorted(Comparator.comparing(VEvent::getStartDate))
                .collect(Collectors.toList());
    }

    public void getAllAheadEvents(String username, String password, Date now, VEventProvider callback){
        new Thread(() -> callback.loaded(getAllAheadVEvents(username, password, now))).start();
    }

    public void tryGetAllAheadEvents(String username, String password, Date now, @NonNull VEventProvider callback){
        try {
            new Thread(() -> {
                try {
                    callback.loaded(getAllAheadVEvents(username, password, now));
                }catch (Exception e){
                    callback.loaded(null);
                }
            }).start();
        }catch (Exception e){
            callback.loaded(null);
        }
    }

    public void tryGetAllEvents(String username, String password, @NonNull VEventProvider callback){
        try {
            new Thread(() -> {
                try {
                    callback.loaded(getAllVEvents(username, password));
                }catch (Exception e){
                    callback.loaded(null);
                }
            }).start();
        }catch (Exception e){
            callback.loaded(null);
        }
    }



    public interface VEventProvider{
        void loaded(List<VEvent> events);
    }

}
