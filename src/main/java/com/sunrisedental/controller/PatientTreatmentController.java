package com.sunrisedental.controller;

import com.sunrisedental.dto.TreatmentRecordResponse;
import com.sunrisedental.service.TreatmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/treatments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientTreatmentController {

    private final TreatmentService treatmentService;

    public PatientTreatmentController(
            TreatmentService treatmentService) {

        this.treatmentService = treatmentService;
    }

    /*
     * =========================================================
     * VIEW MY TREATMENT HISTORY
     * =========================================================
     */

    @GetMapping("/history")
    public ResponseEntity<List<TreatmentRecordResponse>>
    getMyTreatmentHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                treatmentService.getPatientHistory(
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
}
