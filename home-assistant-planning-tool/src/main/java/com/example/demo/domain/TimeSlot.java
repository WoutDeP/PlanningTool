package com.example.demo.domain;

import java.time.LocalTime;

public class TimeSlot {
    private int timeSlotId;
    private LocalTime start;


    public TimeSlot(int id, LocalTime start) {
        this.timeSlotId = id;
        this.start = start;
    }

    public LocalTime getStart() {
        return start;
    }

    public int getTimeSlotId() {
        return timeSlotId;
    }

    @Override
    public String toString() {
        return start.toString();
    }
}
