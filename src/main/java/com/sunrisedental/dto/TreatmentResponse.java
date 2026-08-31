package com.sunrisedental.dto;

import java.math.BigDecimal;

public class TreatmentResponse {

    private Long treatmentId;
    private String name;
    private String description;
    private BigDecimal fee;
    private boolean active;

    public TreatmentResponse() {
    }

    public TreatmentResponse(
            Long treatmentId,
            String name,
            String description,
            BigDecimal fee,
            boolean active) {

        this.treatmentId = treatmentId;
        this.name = name;
        this.description = description;
        this.fee = fee;
        this.active = active;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
