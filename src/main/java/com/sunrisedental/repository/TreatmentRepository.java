package com.sunrisedental.repository;

import com.sunrisedental.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TreatmentRepository
        extends JpaRepository<Treatment, Long> {

    List<Treatment> findByActiveTrueOrderByNameAsc();

    Optional<Treatment> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
