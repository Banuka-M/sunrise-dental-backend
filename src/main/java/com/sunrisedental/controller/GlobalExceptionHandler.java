package com.sunrisedental.controller;

import com.sunrisedental.service.AppointmentConflictException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 409 Conflict
     */
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<?> handleAppointmentConflict(
            AppointmentConflictException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "message",
                                ex.getMessage()
                        )
                );
    }

    /*
     * 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                ex.getMessage()
                        )
                );
    }
}
