package com.sunrisedental.controller;

import com.sunrisedental.dto.AppointmentRequest;
import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.dto.AvailableSlotResponse;
import com.sunrisedental.dto.DentistResponse;
import com.sunrisedental.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patient/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientAppointmentController {

    private final AppointmentService appointmentService;

    public PatientAppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }

    /*
     * =========================================================
     * GET DENTISTS
     * =========================================================
     */

    @GetMapping("/dentists")
    public ResponseEntity<List<DentistResponse>>
    getDentists(
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                appointmentService
                        .getAvailableDentists(date)
        );
    }

    /*
     * =========================================================
     * GET AVAILABLE SLOTS
     * =========================================================
     */

    @GetMapping("/slots")
    public ResponseEntity<List<AvailableSlotResponse>>
    getSlots(
            @RequestParam Long dentistId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                appointmentService
                        .getAvailableSlots(
                                dentistId,
                                date
                        )
        );
    }

    /*
     * =========================================================
     * BOOK
     * =========================================================
     */

    @PostMapping
    public ResponseEntity<AppointmentResponse>
    bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .bookForPatient(
                                request,
                                authentication.getName()
                        )
        );
    }

    /*
     * =========================================================
     * VIEW ALL MY APPOINTMENTS
     * =========================================================
     */

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>>
    getMyAppointments(
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .getPatientAppointments(
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
                        .getPatientAppointment(
                                id,
                                authentication.getName()
                        )
        );
    }

    /*
     * =========================================================
     * UPDATE
     * =========================================================
     */

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
    updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .updateByPatient(
                                id,
                                request,
                                authentication.getName()
                        )
        );
    }

    /*
     * =========================================================
     * CANCEL
     * =========================================================
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
    cancelAppointment(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .cancelByPatient(
                                id,
                                authentication.getName()
                        )
        );
    }
}
