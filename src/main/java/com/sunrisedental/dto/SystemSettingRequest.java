package com.sunrisedental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SystemSettingRequest {

    @NotBlank(message = "Setting key is required")
    @Size(max = 100, message = "Setting key cannot exceed 100 characters")
    private String settingKey;

    @NotBlank(message = "Setting value is required")
    @Size(max = 1000, message = "Setting value cannot exceed 1000 characters")
    private String settingValue;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    public SystemSettingRequest() {
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
