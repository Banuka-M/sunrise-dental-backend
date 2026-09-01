package com.sunrisedental.service;

import com.sunrisedental.dto.NotificationResponse;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Notification;
import com.sunrisedental.model.NotificationType;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.NotificationRepository;
import com.sunrisedental.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    // =========================================================
    // GET ALL NOTIFICATIONS
    // =========================================================

    public List<NotificationResponse> getMyNotifications(
            String username) {

        User user = getUser(username);

        return notificationRepository
                .findByUserUserIdOrderByCreatedAtDesc(
                        user.getUserId()
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET UNREAD NOTIFICATIONS
    // =========================================================

    public List<NotificationResponse> getMyUnreadNotifications(
            String username) {

        User user = getUser(username);

        return notificationRepository
                .findByUserUserIdAndReadFalseOrderByCreatedAtDesc(
                        user.getUserId()
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET UNREAD COUNT
    // =========================================================

    public long getUnreadCount(String username) {

        User user = getUser(username);

        return notificationRepository
                .countByUserUserIdAndReadFalse(
                        user.getUserId()
                );
    }

    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId,
            String username) {

        User user = getUser(username);

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found."
                                )
                        );

        // Make sure this notification belongs
        // to the logged-in user.
        if (!notification.getUser()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You are not allowed to access this notification."
            );
        }

        notification.setRead(true);

        Notification saved =
                notificationRepository.save(notification);

        return convertToResponse(saved);
    }

    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @Transactional
    public void markAllAsRead(String username) {

        User user = getUser(username);

        List<Notification> notifications =
                notificationRepository
                        .findByUserUserIdAndReadFalseOrderByCreatedAtDesc(
                                user.getUserId()
                        );

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    // =========================================================
    // DELETE ONE NOTIFICATION
    // =========================================================

    @Transactional
    public void deleteNotification(
            Long notificationId,
            String username) {

        User user = getUser(username);

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found."
                                )
                        );

        if (!notification.getUser()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You are not allowed to delete this notification."
            );
        }

        notificationRepository.delete(notification);
    }

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @Transactional
    public NotificationResponse createNotification(
            User user,
            String title,
            String message,
            NotificationType type) {

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        Notification saved =
                notificationRepository.save(notification);

        return convertToResponse(saved);
    }

    // =========================================================
// APPOINTMENT BOOKED
// =========================================================

    @Transactional
    public void notifyAppointmentBooked(
            com.sunrisedental.model.Appointment appointment) {

        User patient =
                appointment.getPatient();

        String title =
                "Appointment Booked";

        String message =
                "Your appointment "
                        + appointment.getAppointmentNumber()
                        + " has been booked for "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getAppointmentTime()
                        + " with "
                        + appointment.getDentist().getFullName()
                        + ".";

        createNotificationWithEmail(
                patient,
                title,
                message,
                NotificationType.APPOINTMENT
        );
    }

    // =========================================================
// APPOINTMENT CANCELLED
// =========================================================

    @Transactional
    public void notifyAppointmentCancelled(
            com.sunrisedental.model.Appointment appointment) {

        User patient =
                appointment.getPatient();

        String title =
                "Appointment Cancelled";

        String message =
                "Your appointment "
                        + appointment.getAppointmentNumber()
                        + " scheduled for "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getAppointmentTime()
                        + " has been cancelled.";

        createNotificationWithEmail(
                patient,
                title,
                message,
                NotificationType.APPOINTMENT_CANCELLED
        );
    }

    // =========================================================
// APPOINTMENT UPDATED
// =========================================================

    @Transactional
    public void notifyAppointmentUpdated(
            com.sunrisedental.model.Appointment appointment) {

        User patient =
                appointment.getPatient();

        String title =
                "Appointment Updated";

        String message =
                "Your appointment "
                        + appointment.getAppointmentNumber()
                        + " has been updated. "
                        + "New date: "
                        + appointment.getAppointmentDate()
                        + ", time: "
                        + appointment.getAppointmentTime()
                        + ", dentist: "
                        + appointment.getDentist().getFullName()
                        + ".";

        createNotificationWithEmail(
                patient,
                title,
                message,
                NotificationType.APPOINTMENT
        );
    }


    // =========================================================
    // CREATE NOTIFICATION BY USERNAME
    // =========================================================

    @Transactional
    public NotificationResponse createNotification(
            String username,
            String title,
            String message,
            NotificationType type) {

        User user = getUser(username);

        return createNotification(
                user,
                title,
                message,
                type
        );
    }

    // =========================================================
    // HELPERS
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
    // ENTITY -> RESPONSE
    // =========================================================

    private NotificationResponse convertToResponse(
            Notification notification) {

        NotificationResponse response =
                new NotificationResponse();

        response.setNotificationId(
                notification.getNotificationId()
        );

        response.setTitle(
                notification.getTitle()
        );

        response.setMessage(
                notification.getMessage()
        );

        response.setType(
                notification.getType()
        );

        response.setRead(
                notification.isRead()
        );

        response.setCreatedAt(
                notification.getCreatedAt()
        );

        return response;
    }
       // =========================================================
       // CREATE DATABASE + EMAIL NOTIFICATION
       // =========================================================

    @Transactional
    public NotificationResponse createNotificationWithEmail(
            User user,
            String title,
            String message,
            NotificationType type) {

        NotificationResponse response =
                createNotification(
                        user,
                        title,
                        message,
                        type
                );

        try {

            emailService.sendEmail(
                    user,
                    title,
                    message
            );

        } catch (Exception ex) {

            /*
             * Do not fail the main business operation
             * just because email failed.
             *
             * The database notification has already
             * been created successfully.
             */
            System.err.println(
                    "Failed to send notification email: "
                            + ex.getMessage()
            );
        }

        return response;
    }

    @Transactional
    public void createAppointmentReminder(
            Appointment appointment) {

        Long patientId =
                appointment.getPatient()
                        .getUserId();

        boolean alreadySent =
                notificationRepository
                        .existsByUserUserIdAndReferenceIdAndReferenceType(
                                patientId,
                                appointment.getAppointmentId(),
                                "APPOINTMENT_REMINDER"
                        );

        if (alreadySent) {
            return;
        }

        String title =
                "Appointment Reminder";

        String message =
                "Reminder: You have an appointment "
                        + appointment.getAppointmentNumber()
                        + " tomorrow at "
                        + appointment.getAppointmentTime()
                        + " with "
                        + appointment.getDentist().getFullName()
                        + ".";

        Notification notification = new Notification();

        notification.setUser(
                appointment.getPatient()
        );

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(
                NotificationType.APPOINTMENT
        );

        notification.setRead(false);

        notification.setReferenceId(
                appointment.getAppointmentId()
        );

        notification.setReferenceType(
                "APPOINTMENT_REMINDER"
        );

        notificationRepository.save(notification);

        try {

            emailService.sendEmail(
                    appointment.getPatient(),
                    title,
                    message
            );

        } catch (Exception ex) {

            System.err.println(
                    "Failed to send reminder email: "
                            + ex.getMessage()
            );
        }
    }


}
