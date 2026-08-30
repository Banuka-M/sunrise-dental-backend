package com.sunrisedental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "receptionists")
public class Receptionist extends User {

    public Receptionist() {
        super();
        setRole(Role.RECEPTIONIST);
    }

    public Receptionist(String username,
                        String passwordHash,
                        String fullName,
                        String email) {

        super(username, passwordHash, fullName, email, Role.RECEPTIONIST);
    }
}
