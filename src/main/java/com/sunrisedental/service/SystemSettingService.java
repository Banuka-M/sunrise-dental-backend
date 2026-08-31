package com.sunrisedental.service;

import com.sunrisedental.dto.SystemSettingRequest;
import com.sunrisedental.dto.SystemSettingResponse;
import com.sunrisedental.model.SystemSetting;
import com.sunrisedental.repository.SystemSettingRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingService(
            SystemSettingRepository systemSettingRepository) {

        this.systemSettingRepository = systemSettingRepository;
    }

    // =========================================================
    // GET ALL SETTINGS
    // =========================================================

    public List<SystemSettingResponse> getAllSettings() {

        return systemSettingRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET ONE SETTING
    // =========================================================

    public SystemSettingResponse getSetting(String key) {

        String normalizedKey = normalizeKey(key);

        SystemSetting setting =
                systemSettingRepository
                        .findBySettingKey(normalizedKey)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "System setting not found."
                                )
                        );

        return convertToResponse(setting);
    }

    // =========================================================
    // CREATE SETTING
    // =========================================================

    @Transactional
    public SystemSettingResponse createSetting(
            SystemSettingRequest request) {

        String key =
                normalizeKey(request.getSettingKey());

        if (systemSettingRepository
                .existsBySettingKey(key)) {

            throw new IllegalArgumentException(
                    "A setting with this key already exists."
            );
        }

        SystemSetting setting =
                new SystemSetting();

        setting.setSettingKey(key);
        setting.setSettingValue(
                request.getSettingValue().trim()
        );
        setting.setDescription(
                request.getDescription()
        );

        SystemSetting saved =
                systemSettingRepository.save(setting);

        return convertToResponse(saved);
    }

    // =========================================================
    // UPDATE SETTING
    // =========================================================

    @Transactional
    public SystemSettingResponse updateSetting(
            String key,
            SystemSettingRequest request) {

        String normalizedKey =
                normalizeKey(key);

        SystemSetting setting =
                systemSettingRepository
                        .findBySettingKey(normalizedKey)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "System setting not found."
                                )
                        );

        setting.setSettingValue(
                request.getSettingValue().trim()
        );

        setting.setDescription(
                request.getDescription()
        );

        SystemSetting saved =
                systemSettingRepository.save(setting);

        return convertToResponse(saved);
    }

    // =========================================================
    // DELETE SETTING
    // =========================================================

    @Transactional
    public void deleteSetting(String key) {

        String normalizedKey =
                normalizeKey(key);

        if (!systemSettingRepository
                .existsBySettingKey(normalizedKey)) {

            throw new IllegalArgumentException(
                    "System setting not found."
            );
        }

        systemSettingRepository
                .deleteBySettingKey(normalizedKey);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String normalizeKey(String key) {

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Setting key is required."
            );
        }

        return key.trim().toUpperCase();
    }

    private SystemSettingResponse
    convertToResponse(SystemSetting setting) {

        return new SystemSettingResponse(
                setting.getSettingId(),
                setting.getSettingKey(),
                setting.getSettingValue(),
                setting.getDescription()
        );
    }
}
