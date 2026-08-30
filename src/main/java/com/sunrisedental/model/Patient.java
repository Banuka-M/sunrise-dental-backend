package com.sunrisedental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class Patient extends User {

    private String address;
    private String contactNumber;
    private LocalDate dateOfBirth;

    public Patient() {
        super();
        setRole(Role.PATIENT);
    }

    public Patient(String username, String passwordHash, String fullName, String email,
                   String address, String contactNumber, LocalDate dateOfBirth) {
        super(username, passwordHash, fullName, email, Role.PATIENT);
        this.address = address;
        this.contactNumber = contactNumber;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}