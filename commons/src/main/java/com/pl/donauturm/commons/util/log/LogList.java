package com.pl.donauturm.commons.util.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pl.donauturm.commons.util.lists.LimitedList;

import java.text.ParseException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

public class LogList extends LimitedList<String> {

    private boolean sorting;

    public LogList(int limit) {
        super(limit);
    }

    @Override
    public synchronized boolean add(String s) {
        return super.add(s);
    }

    public synchronized boolean addOld(String s){
        boolean changed = super.add(s);
        sorting = true;
        if (changed)
            sort();
        sorting = false;
        return changed;
    }

    @Override
    public synchronized boolean addAll(@NonNull Collection<? extends String> c) {
        return super.addAll(c);
    }

    public synchronized boolean addAllOld(@NonNull Collection<? extends String> c) {
        boolean changed = super.addAll(c);
        sorting = true;
        if (changed)
            sort();
        sorting = false;
        return changed;
    }

    private synchronized void sort(){
        super.sort(new LogSorter());
    }

    public boolean isSorting() {
        return sorting;
    }

    public static class LogSorter implements Comparator<String>{

        @Override
        public int compare(String log1, String log2) {
            Date date1 = getDateFromLog(log1);
            Date date2 = getDateFromLog(log2);
            if (date1 == null && date2 == null)
                return 0;
            if (date1 == null)
                return -1;
            if (date2 == null)
                return 1;
            return date1.compareTo(date2);
        }

        @Nullable
        public Date getDateFromLog(String log){
            try {
                return Logging.getLogFormat().parse(log);
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
