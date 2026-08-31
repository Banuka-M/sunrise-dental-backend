package com.sunrisedental.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TreatmentAdminRequest {

    @NotBlank(message = "Treatment name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Treatment fee is required")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal fee;

    public TreatmentAdminRequest() {
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
}
