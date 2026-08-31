package com.sunrisedental.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "system_settings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_system_setting_key",
                        columnNames = "setting_key"
                )
        }
)
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settingId;

    @Column(
            name = "setting_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String settingKey;

    @Column(
            name = "setting_value",
            nullable = false,
            length = 1000
    )
    private String settingValue;

    @Column(length = 1000)
    private String description;

    public SystemSetting() {
    }

    public SystemSetting(
            String settingKey,
            String settingValue,
            String description) {

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
