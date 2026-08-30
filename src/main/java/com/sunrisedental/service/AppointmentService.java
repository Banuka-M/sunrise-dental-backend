package com.sunrisedental.service;

import com.sunrisedental.dto.*;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.PatientRepository;
import com.sunrisedental.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final UserRepository userRepository;

    /*
     * Clinic working hours.
     *
     * 09:00 - 17:00
     */
    private static final LocalTime OPENING_TIME =
            LocalTime.of(9, 0);

    private static final LocalTime CLOSING_TIME =
            LocalTime.of(17, 0);

    private static final int APPOINTMENT_DURATION =
            30;

    /*
     * These statuses occupy a dentist's slot.
     *
     * CANCELLED appointments do NOT occupy the slot.
     */
    private static final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(
                    AppointmentStatus.BOOKED,
                    AppointmentStatus.CONFIRMED
            );

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            UserRepository userRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // PATIENT BOOKING
    // =========================================================

    @Transactional
    public AppointmentResponse bookForPatient(
            AppointmentRequest request,
            String username) {

        User user = getUser(username);

        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException(
                    "Only a patient can use patient booking."
            );
        }

        Patient patient = (Patient) user;

        return createAppointment(
                patient,
                patient,
                request
        );
    }

    // =========================================================
    // RECEPTIONIST BOOKING
    // =========================================================

    @Transactional
    public AppointmentResponse bookByReceptionist(
            AppointmentRequest request,
            String username) {

        User receptionist = getUser(username);

        if (receptionist.getRole() != Role.RECEPTIONIST) {
            throw new IllegalArgumentException(
                    "Only a receptionist can book appointments."
            );
        }

        if (request.getPatientId() == null) {
            throw new IllegalArgumentException(
                    "Patient ID is required."
            );
        }

        Patient patient =
                patientRepository
                        .findById(request.getPatientId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Patient not found."
                                )
                        );

        return createAppointment(
                patient,
                receptionist,
                request
        );
    }

    // =========================================================
    // COMMON BOOKING METHOD
    // =========================================================

    private AppointmentResponse createAppointment(
            Patient patient,
            User createdBy,
            AppointmentRequest request) {

        validateDateAndTime(
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        /*
         * Lock dentist row.
         */
        Dentist dentist =
                dentistRepository
                        .findByUserIdForUpdate(
                                request.getDentistId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Dentist not found."
                                )
                        );

        if (!dentist.isActive()) {
            throw new IllegalArgumentException(
                    "Selected dentist is not active."
            );
        }

        /*
         * Check slot.
         */
        boolean alreadyBooked =
                appointmentRepository
                        .existsByDentistUserIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                                dentist.getUserId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime(),
                                ACTIVE_STATUSES
                        );

        if (alreadyBooked) {
            throw new AppointmentConflictException(
                    "This appointment time is already booked."
            );
        }

        Appointment appointment =
                new Appointment();

        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setCreatedBy(createdBy);

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setReason(
                request.getReason()
        );

        appointment.setDescription(
                request.getDescription()
        );

        appointment.setStatus(
                AppointmentStatus.BOOKED
        );

        Appointment saved =
                appointmentRepository.save(appointment);

        return convertToResponse(saved);
    }

    // =========================================================
    // PATIENT VIEW ALL
    // =========================================================

    public List<AppointmentResponse>
    getPatientAppointments(String username) {

        User user = getUser(username);

        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException(
                    "Only patients can access their appointments."
            );
        }

        List<Appointment> appointments =
                appointmentRepository
                        .findByPatientUserIdOrderByAppointmentDateDescAppointmentTimeDesc(
                                user.getUserId()
                        );

        return appointments
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // PATIENT VIEW ONE
    // =========================================================

    public AppointmentResponse
    getPatientAppointment(
            Long appointmentId,
            String username) {

        User user = getUser(username);

        Appointment appointment =
                getAppointment(appointmentId);

        if (!appointment.getPatient()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You can only view your own appointments."
            );
        }

        return convertToResponse(appointment);
    }

    // =========================================================
    // RECEPTIONIST VIEW ALL
    // =========================================================

    public List<AppointmentResponse>
    getAllAppointments() {

        return appointmentRepository
                .findAllByOrderByAppointmentDateDescAppointmentTimeDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // RECEPTIONIST VIEW ONE
    // =========================================================

    public AppointmentResponse
    getReceptionistAppointment(Long appointmentId) {

        Appointment appointment =
                getAppointment(appointmentId);

        return convertToResponse(appointment);
    }

    // =========================================================
    // DENTIST VIEW ALL
    // =========================================================

    public List<AppointmentResponse>
    getDentistAppointments(String username) {

        User user = getUser(username);

        if (!(user instanceof Dentist)) {
            throw new IllegalArgumentException(
                    "Only dentists can access this endpoint."
            );
        }

        List<Appointment> appointments =
                appointmentRepository
                        .findByDentistUserIdOrderByAppointmentDateDescAppointmentTimeDesc(
                                user.getUserId()
                        );

        return appointments
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // DENTIST VIEW ONE
    // =========================================================

    public AppointmentResponse
    getDentistAppointment(
            Long appointmentId,
            String username) {

        User user = getUser(username);

        Appointment appointment =
                getAppointment(appointmentId);

        if (!appointment.getDentist()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You can only view appointments assigned to you."
            );
        }

        return convertToResponse(appointment);
    }

    // =========================================================
    // PATIENT UPDATE
    // =========================================================

    @Transactional
    public AppointmentResponse
    updateByPatient(
            Long appointmentId,
            AppointmentRequest request,
            String username) {

        User user = getUser(username);

        Appointment appointment =
                getAppointment(appointmentId);

        if (!appointment.getPatient()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You can only update your own appointments."
            );
        }

        updateAppointment(
                appointment,
                request
        );

        return convertToResponse(appointment);
    }

    // =========================================================
    // RECEPTIONIST UPDATE
    // =========================================================

    @Transactional
    public AppointmentResponse
    updateByReceptionist(
            Long appointmentId,
            AppointmentRequest request,
            String username) {

        User receptionist = getUser(username);

        if (receptionist.getRole() != Role.RECEPTIONIST) {
            throw new IllegalArgumentException(
                    "Only receptionists can update appointments."
            );
        }

        Appointment appointment =
                getAppointment(appointmentId);

        /*
         * Receptionist can also change patient.
         */
        if (request.getPatientId() != null) {

            Patient patient =
                    patientRepository
                            .findById(request.getPatientId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Patient not found."
                                    )
                            );

            appointment.setPatient(patient);
        }

        updateAppointment(
                appointment,
                request
        );

        return convertToResponse(appointment);
    }

    // =========================================================
    // COMMON UPDATE
    // =========================================================

    private void updateAppointment(
            Appointment appointment,
            AppointmentRequest request) {

        if (appointment.getStatus()
                == AppointmentStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cancelled appointment cannot be updated."
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Completed appointment cannot be updated."
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.NO_SHOW) {

            throw new IllegalArgumentException(
                    "No-show appointment cannot be updated."
            );
        }

        validateDateAndTime(
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        /*
         * Lock the new dentist.
         */
        Dentist dentist =
                dentistRepository
                        .findByUserIdForUpdate(
                                request.getDentistId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Dentist not found."
                                )
                        );

        if (!dentist.isActive()) {
            throw new IllegalArgumentException(
                    "Selected dentist is not active."
            );
        }

        /*
         * Check whether another appointment occupies
         * the new dentist/date/time.
         *
         * The current appointment is excluded.
         */
        boolean alreadyBooked =
                appointmentRepository
                        .existsByDentistUserIdAndAppointmentDateAndAppointmentTimeAndStatusInAndAppointmentIdNot(
                                dentist.getUserId(),
                                request.getAppointmentDate(),
                                request.getAppointmentTime(),
                                ACTIVE_STATUSES,
                                appointment.getAppointmentId()
                        );

        if (alreadyBooked) {
            throw new AppointmentConflictException(
                    "This appointment time is already booked."
            );
        }

        appointment.setDentist(dentist);

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setReason(
                request.getReason()
        );

        appointment.setDescription(
                request.getDescription()
        );

        appointmentRepository.save(appointment);
    }

    // =========================================================
    // PATIENT CANCEL
    // =========================================================

    @Transactional
    public AppointmentResponse
    cancelByPatient(
            Long appointmentId,
            String username) {

        User user = getUser(username);

        Appointment appointment =
                getAppointment(appointmentId);

        if (!appointment.getPatient()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You can only cancel your own appointments."
            );
        }

        cancelAppointment(appointment);

        return convertToResponse(appointment);
    }

    // =========================================================
    // RECEPTIONIST CANCEL
    // =========================================================

    @Transactional
    public AppointmentResponse
    cancelByReceptionist(
            Long appointmentId,
            String username) {

        User user = getUser(username);

        if (user.getRole() != Role.RECEPTIONIST) {
            throw new IllegalArgumentException(
                    "Only receptionists can cancel appointments."
            );
        }

        Appointment appointment =
                getAppointment(appointmentId);

        cancelAppointment(appointment);

        return convertToResponse(appointment);
    }

    // =========================================================
    // COMMON CANCEL
    // =========================================================

    private void cancelAppointment(
            Appointment appointment) {

        if (appointment.getStatus()
                == AppointmentStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Appointment is already cancelled."
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Completed appointment cannot be cancelled."
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.NO_SHOW) {

            throw new IllegalArgumentException(
                    "No-show appointment cannot be cancelled."
            );
        }

        appointment.setStatus(
                AppointmentStatus.CANCELLED
        );

        appointmentRepository.save(appointment);
    }

    // =========================================================
    // GET DENTISTS
    // =========================================================

    public List<DentistResponse>
    getAvailableDentists(LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Date cannot be in the past."
            );
        }

        return dentistRepository
                .findByActiveTrue()
                .stream()
                .map(dentist ->
                        new DentistResponse(
                                dentist.getUserId(),
                                dentist.getFullName(),
                                dentist.getEmail(),
                                dentist.getSpecialization()
                        )
                )
                .toList();
    }

    // =========================================================
    // GET AVAILABLE SLOTS
    // =========================================================

    public List<AvailableSlotResponse>
    getAvailableSlots(
            Long dentistId,
            LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Date cannot be in the past."
            );
        }

        Dentist dentist =
                dentistRepository
                        .findById(dentistId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Dentist not found."
                                )
                        );

        if (!dentist.isActive()) {
            throw new IllegalArgumentException(
                    "Dentist is not active."
            );
        }

        List<Appointment> appointments =
                appointmentRepository
                        .findDentistAppointmentsForDate(
                                dentistId,
                                date,
                                ACTIVE_STATUSES
                        );

        List<AvailableSlotResponse> slots =
                new ArrayList<>();

        LocalTime current =
                OPENING_TIME;

        while (current.isBefore(CLOSING_TIME)) {

            LocalTime slotTime = current;

            boolean booked =
                    appointments
                            .stream()
                            .anyMatch(a ->
                                    a.getAppointmentTime()
                                            .equals(slotTime)
                            );

            boolean passed =
                    date.equals(LocalDate.now())
                            && !slotTime.isAfter(
                            LocalTime.now()
                    );

            slots.add(
                    new AvailableSlotResponse(
                            slotTime,
                            !booked && !passed
                    )
            );

            current =
                    current.plusMinutes(
                            APPOINTMENT_DURATION
                    );
        }

        return slots;
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateDateAndTime(
            LocalDate date,
            LocalTime time) {

        if (date == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (time == null) {
            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        if (time.isBefore(OPENING_TIME)
                || !time.isBefore(CLOSING_TIME)) {

            throw new IllegalArgumentException(
                    "Appointment time must be between 09:00 and 16:30."
            );
        }

        /*
         * Only 00 or 30 minutes are allowed.
         */
        if (time.getMinute() != 0
                && time.getMinute() != 30) {

            throw new IllegalArgumentException(
                    "Appointment time must be in 30-minute intervals."
            );
        }

        if (time.getSecond() != 0
                || time.getNano() != 0) {

            throw new IllegalArgumentException(
                    "Invalid appointment time."
            );
        }

        if (date.equals(LocalDate.now())
                && !time.isAfter(LocalTime.now())) {

            throw new IllegalArgumentException(
                    "Appointment time has already passed."
            );
        }
    }

    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );
    }

    // =========================================================
    // GET APPOINTMENT
    // =========================================================

    private Appointment getAppointment(Long id) {

        return appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Appointment not found."
                        )
                );
    }

    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private AppointmentResponse
    convertToResponse(Appointment appointment) {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setAppointmentId(
                appointment.getAppointmentId()
        );

        response.setAppointmentNumber(
                appointment.getAppointmentNumber()
        );

        response.setAppointmentDate(
                appointment.getAppointmentDate()
        );

        response.setAppointmentTime(
                appointment.getAppointmentTime()
        );

        response.setStatus(
                appointment.getStatus()
        );

        response.setReason(
                appointment.getReason()
        );

        response.setDescription(
                appointment.getDescription()
        );

        Patient patient =
                appointment.getPatient();

        response.setPatientId(
                patient.getUserId()
        );

        response.setPatientName(
                patient.getFullName()
        );

        Dentist dentist =
                appointment.getDentist();

        response.setDentistId(
                dentist.getUserId()
        );

        response.setDentistName(
                dentist.getFullName()
        );

        response.setDentistSpecialization(
                dentist.getSpecialization()
        );

        User createdBy =
                appointment.getCreatedBy();

        response.setCreatedById(
                createdBy.getUserId()
        );

        response.setCreatedByName(
                createdBy.getFullName()
        );

        response.setCreatedByRole(
                createdBy.getRole().name()
        );

        response.setCreatedAt(
                appointment.getCreatedAt()
        );

        return response;
    }
}
