package com.sunrisedental.service;

import com.sunrisedental.dto.*;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.TreatmentRecordRepository;
import com.sunrisedental.repository.TreatmentRepository;

import com.sunrisedental.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreatmentService {


    private final UserRepository userRepository;

    private final TreatmentRepository treatmentRepository;
    private final TreatmentRecordRepository treatmentRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;


    public TreatmentService(
            TreatmentRepository treatmentRepository,
            TreatmentRecordRepository treatmentRecordRepository,
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.treatmentRepository = treatmentRepository;
        this.treatmentRecordRepository =
                treatmentRecordRepository;
        this.appointmentRepository =
                appointmentRepository;
        this.userRepository =
                userRepository;
        this.notificationService =
                notificationService;
    }


    // =========================================================
    // VIEW AVAILABLE TREATMENTS
    // =========================================================

    public List<TreatmentResponse> getActiveTreatments() {

        return treatmentRepository
                .findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::convertTreatmentToResponse)
                .toList();
    }

    // =========================================================
    // ADD TREATMENT RECORD
    // =========================================================

    @Transactional
    public TreatmentRecordResponse addTreatment(
            Long appointmentId,
            TreatmentRequest request,
            String username) {

        Appointment appointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Appointment not found."
                                )
                        );

        User user =
                appointment.getDentist();

        if (!user.getUsername().equals(username)) {
            throw new IllegalArgumentException(
                    "You can only add treatment to your own appointments."
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cancelled appointment cannot receive treatment."
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.NO_SHOW) {

            throw new IllegalArgumentException(
                    "No-show appointment cannot receive treatment."
            );
        }


        Treatment treatment =
                treatmentRepository
                        .findById(request.getTreatmentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Treatment not found."
                                )
                        );

        if (!treatment.isActive()) {
            throw new IllegalArgumentException(
                    "Selected treatment is not active."
            );
        }

        TreatmentRecord record =
                new TreatmentRecord();

        record.setAppointment(appointment);
        record.setPatient(appointment.getPatient());
        record.setDentist(appointment.getDentist());
        record.setTreatment(treatment);

        record.setDiagnosis(
                request.getDiagnosis()
        );

        record.setNotes(
                request.getNotes()
        );

        TreatmentRecord saved =
                treatmentRecordRepository.save(record);

        /*
         * Once the dentist records treatment,
         * the appointment becomes completed.
         */
        appointment.setStatus(
                AppointmentStatus.COMPLETED
        );

        appointmentRepository.save(appointment);

        notificationService.createNotificationWithEmail(
                appointment.getPatient(),
                "Treatment Completed",
                "Your treatment for appointment "
                        + appointment.getAppointmentNumber()
                        + " has been completed by Dr. "
                        + appointment.getDentist().getFullName()
                        + ".",
                NotificationType.APPOINTMENT_COMPLETED
        );


        return convertToResponse(saved);
    }

    // =========================================================
    // VIEW TREATMENT RECORD
    // =========================================================

    public TreatmentRecordResponse
    getTreatmentRecord(
            Long recordId,
            String username) {

        TreatmentRecord record =
                treatmentRecordRepository
                        .findById(recordId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Treatment record not found."
                                )
                        );

        User user =
                getUserFromUsername(username);

        boolean isPatient =
                record.getPatient()
                        .getUserId()
                        .equals(user.getUserId());

        boolean isDentist =
                record.getDentist()
                        .getUserId()
                        .equals(user.getUserId());

        if (!isPatient && !isDentist) {
            throw new IllegalArgumentException(
                    "You are not allowed to view this treatment record."
            );
        }

        return convertToResponse(record);
    }

    // =========================================================
    // DENTIST VIEW PATIENT HISTORY
    // =========================================================

    public List<TreatmentRecordResponse>
    getPatientHistoryForDentist(
            Long patientId,
            String username) {

        /*
         * Verify that the logged-in user is actually a dentist.
         */
        if (!isDentist(username)) {
            throw new IllegalArgumentException(
                    "Only dentists can access patient history this way."
            );
        }

        return treatmentRecordRepository
                .findByPatientUserIdOrderByTreatmentDateDesc(
                        patientId
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // PATIENT VIEW OWN HISTORY
    // =========================================================

    public List<TreatmentRecordResponse>
    getPatientHistory(String username) {

        User user =
                getUserFromUsername(username);

        if (!(user instanceof Patient)) {
            throw new IllegalArgumentException(
                    "Only patients can access their treatment history."
            );
        }

        return treatmentRecordRepository
                .findByPatientUserIdOrderByTreatmentDateDesc(
                        user.getUserId()
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // DENTIST VIEW OWN TREATMENTS
    // =========================================================

    public List<TreatmentRecordResponse>
    getDentistTreatmentRecords(String username) {

        User user =
                getUserFromUsername(username);

        if (!(user instanceof Dentist)) {
            throw new IllegalArgumentException(
                    "Only dentists can access this endpoint."
            );
        }

        return treatmentRecordRepository
                .findByDentistUserIdOrderByTreatmentDateDesc(
                        user.getUserId()
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private User getUserFromUsername(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );
    }

    private User getUserFromRecord(
            TreatmentRecord record) {

        /*
         * The caller can be either the patient or dentist.
         */
        return record.getPatient();
    }

    private boolean isDentist(String username) {

        User user = getUserFromUsername(username);

        return user instanceof Dentist;
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

    private TreatmentRecordResponse
    convertToResponse(
            TreatmentRecord record) {

        TreatmentRecordResponse response =
                new TreatmentRecordResponse();

        response.setRecordId(
                record.getRecordId()
        );

        Appointment appointment =
                record.getAppointment();

        response.setAppointmentId(
                appointment.getAppointmentId()
        );

        response.setAppointmentNumber(
                appointment.getAppointmentNumber()
        );

        Patient patient =
                record.getPatient();

        response.setPatientId(
                patient.getUserId()
        );

        response.setPatientName(
                patient.getFullName()
        );

        Dentist dentist =
                record.getDentist();

        response.setDentistId(
                dentist.getUserId()
        );

        response.setDentistName(
                dentist.getFullName()
        );

        Treatment treatment =
                record.getTreatment();

        response.setTreatmentId(
                treatment.getTreatmentId()
        );

        response.setTreatmentName(
                treatment.getName()
        );

        response.setTreatmentDate(
                record.getTreatmentDate()
        );

        response.setDiagnosis(
                record.getDiagnosis()
        );

        response.setNotes(
                record.getNotes()
        );

        return response;
    }
}
