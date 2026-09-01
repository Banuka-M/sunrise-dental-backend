package com.sunrisedental.repository;

import com.sunrisedental.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository
        extends JpaRepository<Bill, Long> {

    Optional<Bill>
    findByBillNumber(String billNumber);

    Optional<Bill>
    findByTreatmentRecordRecordId(Long recordId);

    List<Bill>
    findByPatientUserIdOrderByCreatedAtDesc(
            Long patientId
    );

    boolean existsByTreatmentRecordRecordId(
            Long recordId
    );
}
