package com.sunrisedental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dentists")
public class Dentist extends User {

    private String specialization;

    public Dentist() {
        super();
        setRole(Role.DENTIST);
    }

    public Dentist(String username,
                   String passwordHash,
                   String fullName,
                   String email,
                   String specialization) {

        super(username, passwordHash, fullName, email, Role.DENTIST);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
