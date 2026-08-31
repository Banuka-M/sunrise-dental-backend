package com.sunrisedental.repository;

import com.sunrisedental.model.Dentist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DentistRepository
        extends JpaRepository<Dentist, Long> {

    List<Dentist> findByActiveTrue();

    @org.springframework.data.jpa.repository.Lock(
            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
    )
    @org.springframework.data.jpa.repository.Query(
            "SELECT d FROM Dentist d WHERE d.userId = :id"
    )
    Optional<Dentist> findByUserIdForUpdate(Long id);
}
