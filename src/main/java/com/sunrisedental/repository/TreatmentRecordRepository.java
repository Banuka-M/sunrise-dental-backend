package com.sunrisedental.repository;

import com.sunrisedental.model.TreatmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TreatmentRecordRepository
        extends JpaRepository<TreatmentRecord, Long> {

    Optional<TreatmentRecord>
    findByAppointmentAppointmentId(Long appointmentId);

    List<TreatmentRecord>
    findByPatientUserIdOrderByTreatmentDateDesc(Long patientId);

    List<TreatmentRecord>
    findByDentistUserIdOrderByTreatmentDateDesc(Long dentistId);
    
}
