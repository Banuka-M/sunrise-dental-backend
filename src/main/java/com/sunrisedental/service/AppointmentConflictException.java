package com.sunrisedental.service;

public class AppointmentConflictException
        extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}
