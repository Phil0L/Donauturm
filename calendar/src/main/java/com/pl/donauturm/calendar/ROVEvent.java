package com.pl.donauturm.calendar;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.Objects;

public class ROVEvent extends VEvent {

    private final VEvent vEvent;
    private final Date startDate;
    private final Date endDate;

    public ROVEvent(VEvent vEvent, Date startDate, Date endDate) {
        super(vEvent.getProperties(), vEvent.getAlarms());
        this.vEvent = vEvent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public final Date getOccurrenceStartDate(){
        return startDate;
    }

    public final Date getOccurrenceEndDate(){
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ROVEvent rovEvent = (ROVEvent) o;
        return startDate.equals(rovEvent.startDate) && vEvent.getSummary().equals(rovEvent.vEvent.getSummary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, vEvent.getSummary());
    }
}
