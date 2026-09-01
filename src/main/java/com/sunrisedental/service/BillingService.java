package com.sunrisedental.service;

import com.sunrisedental.dto.*;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.BillRepository;
import com.sunrisedental.repository.PaymentRepository;
import com.sunrisedental.repository.TreatmentRecordRepository;
import com.sunrisedental.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BillingService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final TreatmentRecordRepository treatmentRecordRepository;
    private final UserRepository userRepository;

    public BillingService(
            BillRepository billRepository,
            PaymentRepository paymentRepository,
            TreatmentRecordRepository treatmentRecordRepository,
            UserRepository userRepository) {

        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.treatmentRecordRepository =
                treatmentRecordRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public BillResponse createBill(
            BillRequest request,
            String username) {

        User user =
                getUser(username);

        if (!(user instanceof Dentist)
                && user.getRole()
                != Role.RECEPTIONIST
                && user.getRole()
                != Role.ADMINISTRATOR) {

            throw new IllegalArgumentException(
                    "You are not allowed to create bills."
            );
        }

        TreatmentRecord record =
                treatmentRecordRepository
                        .findById(request.getRecordId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Treatment record not found."
                                )
                        );

        if (billRepository
                .existsByTreatmentRecordRecordId(
                        record.getRecordId()
                )) {

            throw new IllegalArgumentException(
                    "A bill already exists for this treatment."
            );
        }

        BigDecimal fee =
                record.getTreatment().getFee();

        if (fee == null
                || fee.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Treatment fee must be greater than zero."
            );
        }

        Bill bill =
                new Bill();

        bill.setTreatmentRecord(record);
        bill.setPatient(record.getPatient());
        bill.setTotalAmount(fee);
        bill.setPaidAmount(BigDecimal.ZERO);
        bill.setStatus(BillStatus.UNPAID);

        Bill saved =
                billRepository.save(bill);

        return convertBillToResponse(saved);
    }



    public List<BillResponse>
    getPatientBills(String username) {

        User user =
                getUser(username);

        if (!(user instanceof Patient)) {

            throw new IllegalArgumentException(
                    "Only patients can access their bills."
            );
        }

        return billRepository
                .findByPatientUserIdOrderByCreatedAtDesc(
                        user.getUserId()
                )
                .stream()
                .map(this::convertBillToResponse)
                .toList();
    }


    public BillResponse getPatientBill(
            Long billId,
            String username) {

        User user =
                getUser(username);

        Bill bill =
                getBill(billId);

        if (!bill.getPatient()
                .getUserId()
                .equals(user.getUserId())) {

            throw new IllegalArgumentException(
                    "You can only view your own bills."
            );
        }

        return convertBillToResponse(bill);
    }



    public List<BillResponse> getAllBills(
            String username) {

        User user =
                getUser(username);

        if (user.getRole() != Role.RECEPTIONIST
                && user.getRole() != Role.ADMINISTRATOR) {

            throw new IllegalArgumentException(
                    "You are not allowed to view all bills."
            );
        }

        return billRepository
                .findAll()
                .stream()
                .map(this::convertBillToResponse)
                .toList();
    }


    public BillResponse getStaffBill(
            Long billId,
            String username) {

        User user =
                getUser(username);

        if (user.getRole() != Role.RECEPTIONIST
                && user.getRole() != Role.ADMINISTRATOR) {

            throw new IllegalArgumentException(
                    "You are not allowed to view this bill."
            );
        }

        return convertBillToResponse(
                getBill(billId)
        );
    }



    @Transactional
    public PaymentResponse makePayment(
            Long billId,
            PaymentRequest request,
            String username) {

        User user =
                getUser(username);

        Bill bill =
                getBill(billId);

        boolean patient =
                user instanceof Patient
                        && bill.getPatient()
                        .getUserId()
                        .equals(user.getUserId());

        boolean staff =
                user.getRole()
                        == Role.RECEPTIONIST
                        || user.getRole()
                        == Role.ADMINISTRATOR;

        if (!patient && !staff) {

            throw new IllegalArgumentException(
                    "You are not allowed to make this payment."
            );
        }

        if (bill.getStatus()
                == BillStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "Cancelled bill cannot be paid."
            );
        }

        BigDecimal amount =
                request.getAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero."
            );
        }

        BigDecimal remaining =
                bill.getTotalAmount()
                        .subtract(
                                bill.getPaidAmount()
                        );

        if (amount.compareTo(remaining) > 0) {

            throw new IllegalArgumentException(
                    "Payment cannot exceed the remaining bill amount."
            );
        }

        Payment payment =
                new Payment();

        payment.setBill(bill);
        payment.setAmount(amount);
        payment.setPaymentMethod(
                request.getPaymentMethod()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        BigDecimal newPaidAmount =
                bill.getPaidAmount()
                        .add(amount);

        bill.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(
                bill.getTotalAmount()
        ) == 0) {

            bill.setStatus(
                    BillStatus.PAID
            );

        } else {

            bill.setStatus(
                    BillStatus.PARTIALLY_PAID
            );
        }

        billRepository.save(bill);

        return convertPaymentToResponse(
                savedPayment
        );
    }



    public List<PaymentResponse>
    getPaymentsForBill(
            Long billId,
            String username) {

        User user =
                getUser(username);

        Bill bill =
                getBill(billId);

        boolean patient =
                user instanceof Patient
                        && bill.getPatient()
                        .getUserId()
                        .equals(user.getUserId());

        boolean staff =
                user.getRole()
                        == Role.RECEPTIONIST
                        || user.getRole()
                        == Role.ADMINISTRATOR;

        if (!patient && !staff) {

            throw new IllegalArgumentException(
                    "You are not allowed to view these payments."
            );
        }

        return paymentRepository
                .findByBillBillIdOrderByPaymentDateDesc(
                        billId
                )
                .stream()
                .map(this::convertPaymentToResponse)
                .toList();
    }



    private User getUser(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );
    }

    private Bill getBill(Long billId) {

        return billRepository
                .findById(billId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Bill not found."
                        )
                );
    }


    private BillResponse
    convertBillToResponse(Bill bill) {

        BillResponse response =
                new BillResponse();

        response.setBillId(
                bill.getBillId()
        );

        response.setBillNumber(
                bill.getBillNumber()
        );

        TreatmentRecord record =
                bill.getTreatmentRecord();

        response.setRecordId(
                record.getRecordId()
        );

        response.setAppointmentId(
                record.getAppointment()
                        .getAppointmentId()
        );

        response.setAppointmentNumber(
                record.getAppointment()
                        .getAppointmentNumber()
        );

        Patient patient =
                bill.getPatient();

        response.setPatientId(
                patient.getUserId()
        );

        response.setPatientName(
                patient.getFullName()
        );

        Treatment treatment =
                record.getTreatment();

        response.setTreatmentId(
                treatment.getTreatmentId()
        );

        response.setTreatmentName(
                treatment.getName()
        );

        response.setTotalAmount(
                bill.getTotalAmount()
        );

        response.setPaidAmount(
                bill.getPaidAmount()
        );

        response.setRemainingAmount(
                bill.getTotalAmount()
                        .subtract(
                                bill.getPaidAmount()
                        )
        );

        response.setStatus(
                bill.getStatus()
        );

        response.setCreatedAt(
                bill.getCreatedAt()
        );

        return response;
    }



    private PaymentResponse
    convertPaymentToResponse(
            Payment payment) {

        PaymentResponse response =
                new PaymentResponse();

        response.setPaymentId(
                payment.getPaymentId()
        );

        response.setPaymentNumber(
                payment.getPaymentNumber()
        );

        response.setBillId(
                payment.getBill()
                        .getBillId()
        );

        response.setBillNumber(
                payment.getBill()
                        .getBillNumber()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setPaymentMethod(
                payment.getPaymentMethod()
        );

        response.setPaymentDate(
                payment.getPaymentDate()
        );

        return response;
    }
}
