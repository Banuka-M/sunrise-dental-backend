package com.sunrisedental.repository;

import com.sunrisedental.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemSettingRepository
        extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findBySettingKey(String settingKey);

    boolean existsBySettingKey(String settingKey);

    void deleteBySettingKey(String settingKey);
}
