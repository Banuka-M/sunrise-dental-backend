package com.sunrisedental.dto;

import jakarta.validation.constraints.NotNull;

public class BillRequest {

    @NotNull(message = "Treatment record ID is required")
    private Long recordId;

    public BillRequest() {
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}
