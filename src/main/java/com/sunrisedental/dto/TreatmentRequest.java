package com.sunrisedental.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TreatmentRequest {

    @NotNull(message = "Treatment ID is required")
    private Long treatmentId;

    @Size(max = 2000, message = "Diagnosis cannot exceed 2000 characters")
    private String diagnosis;

    @Size(max = 3000, message = "Notes cannot exceed 3000 characters")
    private String notes;

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
