package com.sunrisedental.repository;

import com.sunrisedental.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment>
    findByBillBillIdOrderByPaymentDateDesc(
            Long billId
    );

    List<Payment>
    findByBillPatientUserIdOrderByPaymentDateDesc(
            Long patientId
    );
}
