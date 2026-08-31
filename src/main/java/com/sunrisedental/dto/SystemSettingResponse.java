package com.sunrisedental.dto;

public class SystemSettingResponse {

    private Long settingId;
    private String settingKey;
    private String settingValue;
    private String description;

    public SystemSettingResponse() {
    }

    public SystemSettingResponse(
            Long settingId,
            String settingKey,
            String settingValue,
            String description) {

        this.settingId = settingId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
    }

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
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
