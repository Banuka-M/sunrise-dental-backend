package com.sunrisedental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReportDTOs {

    // =========================================================
    // APPOINTMENT REPORT
    // =========================================================

    public static class AppointmentReport {

        private Long appointmentId;
        private String appointmentNumber;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String patientName;
        private String dentistName;
        private String status;
        private String reason;

        public AppointmentReport(
                Long appointmentId,
                String appointmentNumber,
                LocalDate appointmentDate,
                LocalTime appointmentTime,
                String patientName,
                String dentistName,
                String status,
                String reason) {

            this.appointmentId = appointmentId;
            this.appointmentNumber = appointmentNumber;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
            this.patientName = patientName;
            this.dentistName = dentistName;
            this.status = status;
            this.reason = reason;
        }

        public Long getAppointmentId() {
            return appointmentId;
        }

        public String getAppointmentNumber() {
            return appointmentNumber;
        }

        public LocalDate getAppointmentDate() {
            return appointmentDate;
        }

        public LocalTime getAppointmentTime() {
            return appointmentTime;
        }

        public String getPatientName() {
            return patientName;
        }

        public String getDentistName() {
            return dentistName;
        }

        public String getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }
    }


    // =========================================================
    // PATIENT REPORT
    // =========================================================

    public static class PatientReport {

        private Long patientId;
        private String patientName;
        private String email;
        private String contactNumber;
        private String address;
        private LocalDate dateOfBirth;
        private boolean active;

        public PatientReport(
                Long patientId,
                String patientName,
                String email,
                String contactNumber,
                String address,
                LocalDate dateOfBirth,
                boolean active) {

            this.patientId = patientId;
            this.patientName = patientName;
            this.email = email;
            this.contactNumber = contactNumber;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
            this.active = active;
        }

        public Long getPatientId() {
            return patientId;
        }

        public String getPatientName() {
            return patientName;
        }

        public String getEmail() {
            return email;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public String getAddress() {
            return address;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public boolean isActive() {
            return active;
        }
    }


    // =========================================================
    // BILLING REPORT
    // =========================================================

    public static class BillingReport {

        private Long billId;
        private String billNumber;
        private String patientName;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal balance;
        private String status;
        private LocalDateTime createdAt;

        public BillingReport(
                Long billId,
                String billNumber,
                String patientName,
                BigDecimal totalAmount,
                BigDecimal paidAmount,
                BigDecimal balance,
                String status,
                LocalDateTime createdAt) {

            this.billId = billId;
            this.billNumber = billNumber;
            this.patientName = patientName;
            this.totalAmount = totalAmount;
            this.paidAmount = paidAmount;
            this.balance = balance;
            this.status = status;
            this.createdAt = createdAt;
        }

        public Long getBillId() {
            return billId;
        }

        public String getBillNumber() {
            return billNumber;
        }

        public String getPatientName() {
            return patientName;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }


    // =========================================================
    // SUMMARY REPORT
    // =========================================================

    public static class SummaryReport {

        private long totalPatients;

        private long totalAppointments;
        private long bookedAppointments;
        private long confirmedAppointments;
        private long completedAppointments;
        private long cancelledAppointments;
        private long noShowAppointments;

        private BigDecimal totalBilling;
        private BigDecimal totalPaid;
        private BigDecimal totalOutstanding;

        public SummaryReport(
                long totalPatients,
                long totalAppointments,
                long bookedAppointments,
                long confirmedAppointments,
                long completedAppointments,
                long cancelledAppointments,
                long noShowAppointments,
                BigDecimal totalBilling,
                BigDecimal totalPaid,
                BigDecimal totalOutstanding) {

            this.totalPatients = totalPatients;
            this.totalAppointments = totalAppointments;
            this.bookedAppointments = bookedAppointments;
            this.confirmedAppointments = confirmedAppointments;
            this.completedAppointments = completedAppointments;
            this.cancelledAppointments = cancelledAppointments;
            this.noShowAppointments = noShowAppointments;
            this.totalBilling = totalBilling;
            this.totalPaid = totalPaid;
            this.totalOutstanding = totalOutstanding;
        }

        public long getTotalPatients() {
            return totalPatients;
        }

        public long getTotalAppointments() {
            return totalAppointments;
        }

        public long getBookedAppointments() {
            return bookedAppointments;
        }

        public long getConfirmedAppointments() {
            return confirmedAppointments;
        }

        public long getCompletedAppointments() {
            return completedAppointments;
        }

        public long getCancelledAppointments() {
            return cancelledAppointments;
        }

        public long getNoShowAppointments() {
            return noShowAppointments;
        }

        public BigDecimal getTotalBilling() {
            return totalBilling;
        }

        public BigDecimal getTotalPaid() {
            return totalPaid;
        }

        public BigDecimal getTotalOutstanding() {
            return totalOutstanding;
        }
    }
}
