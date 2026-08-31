package com.sunrisedental.service;

import com.sunrisedental.dto.*;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.TreatmentRepository;
import com.sunrisedental.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            UserRepository userRepository,
            DentistRepository dentistRepository,
            TreatmentRepository treatmentRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // USER MANAGEMENT
    // =========================================================

    @Transactional
    public AdminUserResponse createUser(
            AdminUserRequest request) {

        if (request.getRole() == Role.DENTIST) {
            throw new IllegalArgumentException(
                    "Use the dentist creation endpoint for dentists."
            );
        }

        if (request.getRole() == Role.ADMINISTRATOR) {
            throw new IllegalArgumentException(
                    "Administrator accounts cannot be created here."
            );
        }

        if (userRepository.existsByUsername(
                request.getUsername())) {

            throw new IllegalArgumentException(
                    "Username already exists."
            );
        }

        String passwordHash =
                passwordEncoder.encode(
                        request.getPassword()
                );

        User user;

        if (request.getRole() == Role.PATIENT) {

            Patient patient = new Patient(
                    request.getUsername(),
                    passwordHash,
                    request.getFullName(),
                    request.getEmail(),
                    request.getAddress(),
                    request.getContactNumber(),
                    request.getDateOfBirth()
            );

            user = patient;

        } else if (request.getRole() == Role.RECEPTIONIST) {

            user = new Receptionist(
                    request.getUsername(),
                    passwordHash,
                    request.getFullName(),
                    request.getEmail()
            );

        } else {

            throw new IllegalArgumentException(
                    "Invalid user role."
            );
        }

        user.setActive(true);

        User saved =
                userRepository.save(user);

        return convertUserToResponse(saved);
    }

    public List<AdminUserResponse> getAllUsers() {

        return userRepository
                .findAllByOrderByUserIdAsc()
                .stream()
                .map(this::convertUserToResponse)
                .toList();
    }

    public AdminUserResponse getUser(Long userId) {

        return convertUserToResponse(
                getUserEntity(userId)
        );
    }

    @Transactional
    public AdminUserResponse updateUser(
            Long userId,
            AdminUserUpdateRequest request) {

        User user =
                getUserEntity(userId);

        user.setFullName(
                request.getFullName()
        );

        user.setEmail(
                request.getEmail()
        );

        if (user instanceof Patient patient) {

            patient.setAddress(
                    request.getAddress()
            );

            patient.setContactNumber(
                    request.getContactNumber()
            );

            patient.setDateOfBirth(
                    request.getDateOfBirth()
            );
        }

        return convertUserToResponse(
                userRepository.save(user)
        );
    }

    @Transactional
    public AdminUserResponse activateUser(
            Long userId) {

        User user =
                getUserEntity(userId);

        user.setActive(true);

        return convertUserToResponse(
                userRepository.save(user)
        );
    }

    @Transactional
    public AdminUserResponse deactivateUser(
            Long userId) {

        User user =
                getUserEntity(userId);

        user.setActive(false);

        return convertUserToResponse(
                userRepository.save(user)
        );
    }

    // =========================================================
    // DENTIST MANAGEMENT
    // =========================================================

    @Transactional
    public AdminUserResponse createDentist(
            AdminDentistRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername())) {

            throw new IllegalArgumentException(
                    "Username already exists."
            );
        }

        String passwordHash =
                passwordEncoder.encode(
                        request.getPassword()
                );

        Dentist dentist =
                new Dentist(
                        request.getUsername(),
                        passwordHash,
                        request.getFullName(),
                        request.getEmail(),
                        request.getSpecialization()
                );

        dentist.setActive(true);

        Dentist saved =
                dentistRepository.save(dentist);

        return convertUserToResponse(saved);
    }

    public List<AdminUserResponse> getAllDentists() {

        return dentistRepository
                .findAll()
                .stream()
                .map(this::convertUserToResponse)
                .toList();
    }

    public AdminUserResponse getDentist(
            Long dentistId) {

        Dentist dentist =
                dentistRepository
                        .findById(dentistId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Dentist not found."
                                )
                        );

        return convertUserToResponse(dentist);
    }

    @Transactional
    public AdminUserResponse updateDentist(
            Long dentistId,
            AdminDentistUpdateRequest request) {

        Dentist dentist =
                dentistRepository
                        .findById(dentistId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Dentist not found."
                                )
                        );

        dentist.setFullName(
                request.getFullName()
        );

        dentist.setEmail(
                request.getEmail()
        );

        dentist.setSpecialization(
                request.getSpecialization()
        );

        return convertUserToResponse(
                dentistRepository.save(dentist)
        );
    }

    @Transactional
    public AdminUserResponse activateDentist(
            Long dentistId) {

        Dentist dentist =
                getDentistEntity(dentistId);

        dentist.setActive(true);

        return convertUserToResponse(
                dentistRepository.save(dentist)
        );
    }

    @Transactional
    public AdminUserResponse deactivateDentist(
            Long dentistId) {

        Dentist dentist =
                getDentistEntity(dentistId);

        dentist.setActive(false);

        return convertUserToResponse(
                dentistRepository.save(dentist)
        );
    }

    // =========================================================
    // TREATMENT MANAGEMENT
    // =========================================================

    public List<TreatmentResponse> getAllTreatments() {

        return treatmentRepository
                .findAll()
                .stream()
                .map(this::convertTreatmentToResponse)
                .toList();
    }

    public TreatmentResponse getTreatment(
            Long treatmentId) {

        return convertTreatmentToResponse(
                getTreatmentEntity(treatmentId)
        );
    }

    @Transactional
    public TreatmentResponse createTreatment(
            TreatmentAdminRequest request) {

        if (treatmentRepository
                .existsByNameIgnoreCase(
                        request.getName())) {

            throw new IllegalArgumentException(
                    "Treatment name already exists."
            );
        }

        Treatment treatment =
                new Treatment(
                        request.getName(),
                        request.getDescription(),
                        request.getFee()
                );

        treatment.setActive(true);

        return convertTreatmentToResponse(
                treatmentRepository.save(treatment)
        );
    }

    @Transactional
    public TreatmentResponse updateTreatment(
            Long treatmentId,
            TreatmentAdminRequest request) {

        Treatment treatment =
                getTreatmentEntity(treatmentId);

        treatmentRepository
                .findByNameIgnoreCase(
                        request.getName()
                )
                .ifPresent(existing -> {

                    if (!existing.getTreatmentId()
                            .equals(treatmentId)) {

                        throw new IllegalArgumentException(
                                "Treatment name already exists."
                        );
                    }
                });

        treatment.setName(
                request.getName()
        );

        treatment.setDescription(
                request.getDescription()
        );

        treatment.setFee(
                request.getFee()
        );

        return convertTreatmentToResponse(
                treatmentRepository.save(treatment)
        );
    }

    @Transactional
    public TreatmentResponse activateTreatment(
            Long treatmentId) {

        Treatment treatment =
                getTreatmentEntity(treatmentId);

        treatment.setActive(true);

        return convertTreatmentToResponse(
                treatmentRepository.save(treatment)
        );
    }

    @Transactional
    public TreatmentResponse deactivateTreatment(
            Long treatmentId) {

        Treatment treatment =
                getTreatmentEntity(treatmentId);

        treatment.setActive(false);

        return convertTreatmentToResponse(
                treatmentRepository.save(treatment)
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private User getUserEntity(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );
    }

    private Dentist getDentistEntity(
            Long dentistId) {

        return dentistRepository
                .findById(dentistId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Dentist not found."
                        )
                );
    }

    private Treatment getTreatmentEntity(
            Long treatmentId) {

        return treatmentRepository
                .findById(treatmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Treatment not found."
                        )
                );
    }

    private AdminUserResponse convertUserToResponse(
            User user) {

        String address = null;
        String contactNumber = null;
        java.time.LocalDate dateOfBirth = null;

        if (user instanceof Patient patient) {

            address = patient.getAddress();
            contactNumber = patient.getContactNumber();
            dateOfBirth = patient.getDateOfBirth();
        }

        return new AdminUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                address,
                contactNumber,
                dateOfBirth
        );
    }

    private TreatmentResponse
    convertTreatmentToResponse(
            Treatment treatment) {

        return new TreatmentResponse(
                treatment.getTreatmentId(),
                treatment.getName(),
                treatment.getDescription(),
                treatment.getFee(),
                treatment.isActive()
        );
    }
}
