package com.sunrisedental.controller;

import com.sunrisedental.dto.AdminUserResponse;
import com.sunrisedental.dto.AdminUserUpdateRequest;
import com.sunrisedental.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientProfileController {

    private final PatientService patientService;

    public PatientProfileController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/profile")
    public ResponseEntity<AdminUserResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(
                patientService.getProfile(authentication.getName())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<AdminUserResponse> updateProfile(
            @Valid @RequestBody AdminUserUpdateRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                patientService.updateProfile(request, authentication.getName())
        );
    }
}