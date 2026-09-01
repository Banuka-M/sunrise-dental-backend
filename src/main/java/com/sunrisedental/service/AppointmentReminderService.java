package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.repository.AppointmentRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentReminderService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    public AppointmentReminderService(
            AppointmentRepository appointmentRepository,
            NotificationService notificationService) {

        this.appointmentRepository =
                appointmentRepository;

        this.notificationService =
                notificationService;
    }

    // =========================================================
    // CHECK EVERY 10 MINUTES
    // =========================================================

    @Scheduled(
            fixedRate = 600000
    )
    public void sendAppointmentReminders() {

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime reminderStart =
                now.plusHours(23);

        LocalDateTime reminderEnd =
                now.plusHours(25);

        List<Appointment> appointments =
                appointmentRepository
                        .findByAppointmentDateBetween(
                                reminderStart.toLocalDate(),
                                reminderEnd.toLocalDate()
                        );


        for (Appointment appointment :
                appointments) {

            if (!isActive(appointment)) {
                continue;
            }

            LocalDateTime appointmentDateTime =
                    LocalDateTime.of(
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime()
                    );

            if (!appointmentDateTime.isBefore(reminderStart)
                    && appointmentDateTime.isBefore(reminderEnd)) {

                sendReminder(appointment);
            }
        }
    }

    // =========================================================
    // SEND REMINDER
    // =========================================================

    private void sendReminder(
            Appointment appointment) {

        notificationService.createAppointmentReminder(
                appointment
        );
    }

    // =========================================================
    // ACTIVE APPOINTMENT
    // =========================================================

    private boolean isActive(
            Appointment appointment) {

        return appointment.getStatus()
                == AppointmentStatus.BOOKED
                || appointment.getStatus()
                == AppointmentStatus.CONFIRMED;
    }
}
