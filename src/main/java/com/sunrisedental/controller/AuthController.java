package com.sunrisedental.controller;

import com.sunrisedental.dto.AuthDTOs.*;
import com.sunrisedental.dto.ChangePasswordRequest;
import com.sunrisedental.dto.PatientRegisterRequest;
import com.sunrisedental.model.Patient;
import com.sunrisedental.security.JwtUtils;
import com.sunrisedental.service.AccountService;
import com.sunrisedental.service.PasswordResetService;
import com.sunrisedental.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordResetService passwordResetService;



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst().get().getAuthority();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), role));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        Patient registeredPatient = patientService.registerPatient(request);
        return ResponseEntity.ok("Patient registered successfully with ID: " + registeredPatient.getUserId());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        accountService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully."
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.forgotPassword(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "Password reset email sent successfully."
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password reset successfully."
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        return ResponseEntity.ok(
                "Logged out successfully."
        );
    }


}