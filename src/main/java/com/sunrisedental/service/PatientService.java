package com.sunrisedental.service;

import com.sunrisedental.dto.PatientRegisterRequest;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.Role;
import com.sunrisedental.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}