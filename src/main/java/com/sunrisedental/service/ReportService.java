package com.sunrisedental.service;

import com.sunrisedental.dto.ReportDTOs.*;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
import com.sunrisedental.repository.PatientRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final BillRepository billRepository;

    public ReportService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            BillRepository billRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.billRepository = billRepository;
    }


    // =========================================================
    // APPOINTMENT REPORT
    // =========================================================

    public List<AppointmentReport> getAppointmentReport(
            LocalDate startDate,
            LocalDate endDate) {

        List<Appointment> appointments;

        if (startDate != null && endDate != null) {

            appointments =
                    appointmentRepository
                            .findByAppointmentDateBetween(
                                    startDate,
                                    endDate
                            );

        } else {

            appointments =
                    appointmentRepository
                            .findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
        }

        return appointments.stream()
                .map(this::convertAppointment)
                .collect(Collectors.toList());
    }


    private AppointmentReport convertAppointment(
            Appointment appointment) {

        return new AppointmentReport(

                appointment.getAppointmentId(),

                appointment.getAppointmentNumber(),

                appointment.getAppointmentDate(),

                appointment.getAppointmentTime(),

                appointment.getPatient()
                        .getFullName(),

                appointment.getDentist()
                        .getFullName(),

                appointment.getStatus()
                        .name(),

                appointment.getReason()
        );
    }


    // =========================================================
    // PATIENT REPORT
    // =========================================================

    public List<PatientReport> getPatientReport() {

        return patientRepository.findAll()
                .stream()
                .map(patient -> new PatientReport(

                        patient.getUserId(),

                        patient.getFullName(),

                        patient.getEmail(),

                        patient.getContactNumber(),

                        patient.getAddress(),

                        patient.getDateOfBirth(),

                        patient.isActive()

                ))
                .collect(Collectors.toList());
    }


    // =========================================================
    // BILLING REPORT
    // =========================================================

    public List<BillingReport> getBillingReport() {

        return billRepository.findAll()
                .stream()
                .map(this::convertBill)
                .collect(Collectors.toList());
    }


    private BillingReport convertBill(Bill bill) {

        BigDecimal total =
                bill.getTotalAmount() != null
                        ? bill.getTotalAmount()
                        : BigDecimal.ZERO;

        BigDecimal paid =
                bill.getPaidAmount() != null
                        ? bill.getPaidAmount()
                        : BigDecimal.ZERO;

        BigDecimal balance =
                total.subtract(paid);

        return new BillingReport(

                bill.getBillId(),

                bill.getBillNumber(),

                bill.getPatient()
                        .getFullName(),

                total,

                paid,

                balance,

                bill.getStatus()
                        .name(),

                bill.getCreatedAt()
        );
    }


    // =========================================================
    // SUMMARY REPORT
    // =========================================================

    public SummaryReport getSummaryReport() {

        long totalPatients =
                patientRepository.count();

        List<Appointment> appointments =
                appointmentRepository.findAll();

        long totalAppointments =
                appointments.size();

        long booked =
                countStatus(
                        appointments,
                        AppointmentStatus.BOOKED
                );

        long confirmed =
                countStatus(
                        appointments,
                        AppointmentStatus.CONFIRMED
                );

        long completed =
                countStatus(
                        appointments,
                        AppointmentStatus.COMPLETED
                );

        long cancelled =
                countStatus(
                        appointments,
                        AppointmentStatus.CANCELLED
                );

        long noShow =
                countStatus(
                        appointments,
                        AppointmentStatus.NO_SHOW
                );


        List<Bill> bills =
                billRepository.findAll();

        BigDecimal totalBilling =
                bills.stream()
                        .map(Bill::getTotalAmount)
                        .filter(amount -> amount != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalPaid =
                bills.stream()
                        .map(Bill::getPaidAmount)
                        .filter(amount -> amount != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalOutstanding =
                totalBilling.subtract(totalPaid);


        return new SummaryReport(

                totalPatients,

                totalAppointments,

                booked,

                confirmed,

                completed,

                cancelled,

                noShow,

                totalBilling,

                totalPaid,

                totalOutstanding
        );
    }


    // =========================================================
    // COUNT STATUS
    // =========================================================

    private long countStatus(
            List<Appointment> appointments,
            AppointmentStatus status) {

        return appointments.stream()
                .filter(a ->
                        a.getStatus() == status
                )
                .count();
    }
}
