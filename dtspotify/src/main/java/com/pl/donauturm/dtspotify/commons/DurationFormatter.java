package com.pl.donauturm.dtspotify.commons;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DurationFormatter {

    public static String minSec(long duration, String format){
        if (duration < 0) return "";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long remaining = duration - TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining);
        return String.format(Locale.getDefault(), format, minutes, seconds);
    }

    public static String min(long duration, String format){
        if (duration < 0) return "";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        return String.format(Locale.getDefault(), format, minutes);
    }

    public static String sec(long duration, String format){
        if (duration < 0) return "";
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        return String.format(Locale.getDefault(), format, seconds);
    }

    public static String dynamic(long duration, String format){
        if (duration < 0) return "";
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        if (hours > 0) return String.format(Locale.getDefault(), format, hours + "h");
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        if (minutes > 0) return String.format(Locale.getDefault(), format, minutes + "m");
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        return String.format(Locale.getDefault(), format, seconds + "s");
    }
}
