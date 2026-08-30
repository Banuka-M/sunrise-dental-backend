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
@RequestMapping("/api/receptionist/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReceptionistAppointmentController {

    private final AppointmentService appointmentService;

    public ReceptionistAppointmentController(
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
     * GET SLOTS
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
     * BOOK FOR PATIENT
     * =========================================================
     */

    @PostMapping
    public ResponseEntity<AppointmentResponse>
    bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService
                        .bookByReceptionist(
                                request,
                                authentication.getName()
                        )
        );
    }

    /*
     * =========================================================
     * VIEW ALL
     * =========================================================
     */

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>>
    getAllAppointments() {

        return ResponseEntity.ok(
                appointmentService
                        .getAllAppointments()
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
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService
                        .getReceptionistAppointment(id)
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
                        .updateByReceptionist(
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
                        .cancelByReceptionist(
                                id,
                                authentication.getName()
                        )
        );
    }
}
