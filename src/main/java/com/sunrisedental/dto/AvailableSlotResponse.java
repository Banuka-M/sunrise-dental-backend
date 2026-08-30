package com.sunrisedental.dto;

import java.time.LocalTime;

public class AvailableSlotResponse {

    private LocalTime time;
    private boolean available;

    public AvailableSlotResponse() {
    }

    public AvailableSlotResponse(
            LocalTime time,
            boolean available) {

        this.time = time;
        this.available = available;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
