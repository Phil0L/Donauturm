package com.pl.donauturm.drinksmenu.model.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public interface Id {

    String getId();

    long getIdLong();

    Id createNewId();

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.CONSTRUCTOR})
    @interface CreatorConstructor {

    }

    default String newId() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm", Locale.GERMANY);
        String formattedDate = sdf.format(date);
        int random = (int) (Math.random() * 1000);
        return formattedDate + String.format(Locale.GERMANY, "%03d", random);
    }

    default long newIdLong() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm", Locale.GERMANY);
        String formattedDate = sdf.format(date);
        int random = (int) (Math.random() * 1000);
        return Long.parseLong(formattedDate + String.format(Locale.GERMANY, "%03d", random));
    }

}
