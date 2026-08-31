package com.sunrisedental.controller;

import com.sunrisedental.dto.TreatmentRecordResponse;
import com.sunrisedental.dto.TreatmentRequest;
import com.sunrisedental.dto.TreatmentResponse;
import com.sunrisedental.service.TreatmentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentist/treatments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DentistTreatmentController {

    private final TreatmentService treatmentService;

    public DentistTreatmentController(
            TreatmentService treatmentService) {

        this.treatmentService = treatmentService;
    }

    /*
     * =========================================================
     * VIEW AVAILABLE TREATMENTS
     * =========================================================
     */

    @GetMapping("/types")
    public ResponseEntity<List<TreatmentResponse>>
    getTreatmentTypes() {

        return ResponseEntity.ok(
                treatmentService.getActiveTreatments()
        );
    }

    /*
     * =========================================================
     * ADD TREATMENT TO APPOINTMENT
     * =========================================================
     */

    @PostMapping("/appointments/{appointmentId}")
    public ResponseEntity<TreatmentRecordResponse>
    addTreatment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody TreatmentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                treatmentService.addTreatment(
                        appointmentId,
                        request,
                        authentication.getName()
                )
        );
    }

    /*
     * =========================================================
     * VIEW ONE TREATMENT RECORD
     * =========================================================
     */

    @GetMapping("/{recordId}")
    public ResponseEntity<TreatmentRecordResponse>
    getTreatmentRecord(
            @PathVariable Long recordId,
            Authentication authentication) {

        return ResponseEntity.ok(
                treatmentService.getTreatmentRecord(
                        recordId,
                        authentication.getName()
                )
        );
    }

    /*
     * =========================================================
     * VIEW PATIENT HISTORY
     * =========================================================
     */

    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<TreatmentRecordResponse>>
    getPatientHistory(
            @PathVariable Long patientId,
            Authentication authentication) {

        return ResponseEntity.ok(
                treatmentService.getPatientHistoryForDentist(
                        patientId,
                        authentication.getName()
                )
        );
    }

    /*
     * =========================================================
     * VIEW MY TREATMENT RECORDS
     * =========================================================
     */

    @GetMapping("/my-records")
    public ResponseEntity<List<TreatmentRecordResponse>>
    getMyTreatmentRecords(
            Authentication authentication) {

        return ResponseEntity.ok(
                treatmentService.getDentistTreatmentRecords(
                        authentication.getName()
                )
        );
    }
}
