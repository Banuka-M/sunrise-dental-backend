package com.sunrisedental.repository;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    /*
     * Check whether a dentist has an active appointment
     * at a specific date and time.
     */
    boolean existsByDentistUserIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Long dentistId,
            LocalDate date,
            LocalTime time,
            List<AppointmentStatus> statuses
    );

    /*
     * Same check, but exclude a particular appointment.
     *
     * Used when updating an existing appointment.
     */
    boolean existsByDentistUserIdAndAppointmentDateAndAppointmentTimeAndStatusInAndAppointmentIdNot(
            Long dentistId,
            LocalDate date,
            LocalTime time,
            List<AppointmentStatus> statuses,
            Long appointmentId
    );

    /*
     * All appointments belonging to a patient.
     */
    List<Appointment>
    findByPatientUserIdOrderByAppointmentDateDescAppointmentTimeDesc(
            Long patientId
    );

    /*
     * All appointments assigned to a dentist.
     */
    List<Appointment>
    findByDentistUserIdOrderByAppointmentDateDescAppointmentTimeDesc(
            Long dentistId
    );

    /*
     * All appointments.
     */
    List<Appointment>
    findAllByOrderByAppointmentDateDescAppointmentTimeDesc();

    /*
     * Find using appointment number.
     */
    Optional<Appointment>
    findByAppointmentNumber(String appointmentNumber);

    /*
     * Dentist appointments for a particular date.
     */
    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.dentist.userId = :dentistId
            AND a.appointmentDate = :date
            AND a.status IN :statuses
            ORDER BY a.appointmentTime
            """)
    List<Appointment> findDentistAppointmentsForDate(
            Long dentistId,
            LocalDate date,
            List<AppointmentStatus> statuses
    );
}
