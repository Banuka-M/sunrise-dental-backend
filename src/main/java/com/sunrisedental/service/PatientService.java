package com.sunrisedental.service;

import com.sunrisedental.dto.PatientRegisterRequest;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.Role;
import com.sunrisedental.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sunrisedental.dto.AdminUserResponse;
import com.sunrisedental.dto.AdminUserUpdateRequest;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Patient registerPatient(PatientRegisterRequest request) {
        if (patientRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        Patient patient = new Patient();
        patient.setUsername(request.getUsername());
        patient.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        patient.setFullName(request.getFullName());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setContactNumber(request.getContactNumber());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setRole(Role.PATIENT);

        return patientRepository.save(patient);
    }

    @Autowired
    private UserRepository userRepository;

    public AdminUserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException("Only patients can access this endpoint.");
        }

        Patient patient = (Patient) user;
        return convertToAdminUserResponse(patient);
    }

    public AdminUserResponse updateProfile(AdminUserUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException("Only patients can access this endpoint.");
        }

        Patient patient = (Patient) user;

        patient.setFullName(request.getFullName());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setContactNumber(request.getContactNumber());
        patient.setDateOfBirth(request.getDateOfBirth());

        Patient saved = patientRepository.save(patient);
        return convertToAdminUserResponse(saved);
    }

    private AdminUserResponse convertToAdminUserResponse(Patient patient) {
        return new AdminUserResponse(
                patient.getUserId(),
                patient.getUsername(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getRole(),
                patient.isActive(),
                patient.getAddress(),
                patient.getContactNumber(),
                patient.getDateOfBirth()
        );
    }
}