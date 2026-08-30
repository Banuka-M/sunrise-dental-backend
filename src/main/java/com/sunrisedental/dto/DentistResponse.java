package com.sunrisedental.dto;

public class DentistResponse {

    private Long dentistId;
    private String fullName;
    private String email;
    private String specialization;

    public DentistResponse() {
    }

    public DentistResponse(
            Long dentistId,
            String fullName,
            String email,
            String specialization) {

        this.dentistId = dentistId;
        this.fullName = fullName;
        this.email = email;
        this.specialization = specialization;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
