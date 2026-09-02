package com.sunrisedental.controller;

import com.sunrisedental.dto.ReportDTOs.*;
import com.sunrisedental.service.ReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService) {

        this.reportService = reportService;
    }


    // =========================================================
    // APPOINTMENT REPORT
    // =========================================================

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentReport>>
    getAppointmentReport(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                reportService.getAppointmentReport(
                        startDate,
                        endDate
                )
        );
    }


    // =========================================================
    // PATIENT REPORT
    // =========================================================

    @GetMapping("/patients")
    public ResponseEntity<List<PatientReport>>
    getPatientReport() {

        return ResponseEntity.ok(
                reportService.getPatientReport()
        );
    }


    // =========================================================
    // BILLING REPORT
    // =========================================================

    @GetMapping("/billing")
    public ResponseEntity<List<BillingReport>>
    getBillingReport() {

        return ResponseEntity.ok(
                reportService.getBillingReport()
        );
    }


    // =========================================================
    // SUMMARY REPORT
    // =========================================================

    @GetMapping("/summary")
    public ResponseEntity<SummaryReport>
    getSummaryReport() {

        return ResponseEntity.ok(
                reportService.getSummaryReport()
        );
    }
}
