package com.pl.donauturm.calendar.view;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.pl.donauturm.calendar.R;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.Strings;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

public class VEventView extends MaterialCardView {

    private VEvent vEvent;
    private String name;
    private String offsetDateTime;
    private Date startDate;
    private Date endDate;
    private int dateMode = 0; // 0 = offset, 1 = format

    private final TextView offsetDateTimeView;
    private final TextView summaryView;

    public VEventView(Context context) {
        this(context, null);
    }

    public VEventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public VEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        name = "Loading...";
        offsetDateTime = "Now";
        View view = inflate(getContext(), R.layout.view_vevent, this);
        offsetDateTimeView = view.findViewById(R.id.offsetDT);
        offsetDateTimeView.setOnClickListener(v -> this.switchDateMode());
        summaryView = view.findViewById(R.id.summary);
        notifyDataChanged();
    }

    private void switchDateMode() {
        if (dateMode == 0) dateMode = 1;
        else dateMode = 0;
        this.notifyDataChanged();
    }

    public void setVEvent(@Nullable VEvent event) {
        if (event == null) {
            setError();
            return;
        }
        this.vEvent = event;
        this.name = event.getSummary().getValue();
        this.offsetDateTime = getDateString(event.getStartDate().getDate());
        this.startDate = event.getStartDate().getDate();
        this.endDate = event.getEndDate().getDate();
        this.notifyDataChanged();
    }

    public void setError(Exception exception) {
        this.name = exception.getLocalizedMessage();
        this.offsetDateTime = "Now";
        this.notifyDataChanged();
    }

    public void setError(String errorName) {
        this.name = errorName;
        this.offsetDateTime = "Now";
        this.notifyDataChanged();
    }

    public void setError() {
        this.name = "Connection error";
        this.offsetDateTime = "Now";
        this.notifyDataChanged();
    }

    private String getDateString(Date date){
        if (dateMode == 0) return calculateOffset(date);
        return formatDate(date);
    }

    private String calculateOffset(Date date) {
        try {
            PrettyTime prettyTime = new PrettyTime();
            return prettyTime.format(date);
        } catch (Exception e) {
            return Strings.valueOf(date);
        }
    }

    private String formatDate(Date date) {
        try {
            String pattern = "dd. MMMM yyyy HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return Strings.valueOf(date);
        }
    }

    private void notifyDataChanged() {
        this.offsetDateTime = getDateString(startDate != null ? startDate : Calendar.getInstance().getTime());
        this.offsetDateTimeView.setText(offsetDateTime);
        this.summaryView.setText(name);
    }

}
