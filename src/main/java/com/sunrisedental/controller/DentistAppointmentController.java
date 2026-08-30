package com.sunrisedental.controller;

import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.service.AppointmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentist/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DentistAppointmentController {

    private final AppointmentService appointmentService;

    public DentistAppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }

    /*
     * =========================================================
     * VIEW MY APPOINTMENTS
     * =========================================================
     */

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>>
    getMyAppointments(
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .getDentistAppointments(
                                authentication.getName()
                        )
        );
    }

    /*
     * =========================================================
     * VIEW ONE
     * =========================================================
     */

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
    getAppointment(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .getDentistAppointment(
                                id,
                                authentication.getName()
                        )
        );
    }
}
