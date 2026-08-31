package com.sunrisedental.controller;

import com.sunrisedental.dto.*;
import com.sunrisedental.service.AdminService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final AdminService adminService;

    public AdminController(
            AdminService adminService) {

        this.adminService = adminService;
    }

    // =========================================================
    // USER MANAGEMENT
    // =========================================================

    @PostMapping("/users")
    public ResponseEntity<AdminUserResponse>
    createUser(
            @Valid @RequestBody AdminUserRequest request) {

        return ResponseEntity.ok(
                adminService.createUser(request)
        );
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>>
    getAllUsers() {

        return ResponseEntity.ok(
                adminService.getAllUsers()
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse>
    getUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminService.getUser(userId)
        );
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse>
    updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateRequest request) {

        return ResponseEntity.ok(
                adminService.updateUser(
                        userId,
                        request
                )
        );
    }

    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<AdminUserResponse>
    activateUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminService.activateUser(userId)
        );
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<AdminUserResponse>
    deactivateUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminService.deactivateUser(userId)
        );
    }

    // =========================================================
    // DENTIST MANAGEMENT
    // =========================================================

    @PostMapping("/dentists")
    public ResponseEntity<AdminUserResponse>
    createDentist(
            @Valid @RequestBody AdminDentistRequest request) {

        return ResponseEntity.ok(
                adminService.createDentist(request)
        );
    }

    @GetMapping("/dentists")
    public ResponseEntity<List<AdminUserResponse>>
    getAllDentists() {

        return ResponseEntity.ok(
                adminService.getAllDentists()
        );
    }

    @GetMapping("/dentists/{dentistId}")
    public ResponseEntity<AdminUserResponse>
    getDentist(
            @PathVariable Long dentistId) {

        return ResponseEntity.ok(
                adminService.getDentist(dentistId)
        );
    }

    @PutMapping("/dentists/{dentistId}")
    public ResponseEntity<AdminUserResponse>
    updateDentist(
            @PathVariable Long dentistId,
            @Valid @RequestBody AdminDentistUpdateRequest request) {

        return ResponseEntity.ok(
                adminService.updateDentist(
                        dentistId,
                        request
                )
        );
    }

    @PutMapping("/dentists/{dentistId}/activate")
    public ResponseEntity<AdminUserResponse>
    activateDentist(
            @PathVariable Long dentistId) {

        return ResponseEntity.ok(
                adminService.activateDentist(dentistId)
        );
    }

    @PutMapping("/dentists/{dentistId}/deactivate")
    public ResponseEntity<AdminUserResponse>
    deactivateDentist(
            @PathVariable Long dentistId) {

        return ResponseEntity.ok(
                adminService.deactivateDentist(dentistId)
        );
    }

    // =========================================================
    // TREATMENT MANAGEMENT
    // =========================================================

    @PostMapping("/treatments")
    public ResponseEntity<TreatmentResponse>
    createTreatment(
            @Valid @RequestBody TreatmentAdminRequest request) {

        return ResponseEntity.ok(
                adminService.createTreatment(request)
        );
    }

    @GetMapping("/treatments")
    public ResponseEntity<List<TreatmentResponse>>
    getAllTreatments() {

        return ResponseEntity.ok(
                adminService.getAllTreatments()
        );
    }

    @GetMapping("/treatments/{treatmentId}")
    public ResponseEntity<TreatmentResponse>
    getTreatment(
            @PathVariable Long treatmentId) {

        return ResponseEntity.ok(
                adminService.getTreatment(treatmentId)
        );
    }

    @PutMapping("/treatments/{treatmentId}")
    public ResponseEntity<TreatmentResponse>
    updateTreatment(
            @PathVariable Long treatmentId,
            @Valid @RequestBody TreatmentAdminRequest request) {

        return ResponseEntity.ok(
                adminService.updateTreatment(
                        treatmentId,
                        request
                )
        );
    }

    @PutMapping("/treatments/{treatmentId}/activate")
    public ResponseEntity<TreatmentResponse>
    activateTreatment(
            @PathVariable Long treatmentId) {

        return ResponseEntity.ok(
                adminService.activateTreatment(
                        treatmentId
                )
        );
    }

    @PutMapping("/treatments/{treatmentId}/deactivate")
    public ResponseEntity<TreatmentResponse>
    deactivateTreatment(
            @PathVariable Long treatmentId) {

        return ResponseEntity.ok(
                adminService.deactivateTreatment(
                        treatmentId
                )
        );
    }
}
