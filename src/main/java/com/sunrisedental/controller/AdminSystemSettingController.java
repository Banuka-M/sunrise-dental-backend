package com.sunrisedental.controller;

import com.sunrisedental.dto.SystemSettingRequest;
import com.sunrisedental.dto.SystemSettingResponse;
import com.sunrisedental.service.SystemSettingService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminSystemSettingController {

    private final SystemSettingService systemSettingService;

    public AdminSystemSettingController(
            SystemSettingService systemSettingService) {

        this.systemSettingService = systemSettingService;
    }

    // =========================================================
    // GET ALL SETTINGS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SystemSettingResponse>>
    getAllSettings() {

        return ResponseEntity.ok(
                systemSettingService.getAllSettings()
        );
    }

    // =========================================================
    // GET ONE SETTING
    // =========================================================

    @GetMapping("/{key}")
    public ResponseEntity<SystemSettingResponse>
    getSetting(
            @PathVariable String key) {

        return ResponseEntity.ok(
                systemSettingService.getSetting(key)
        );
    }

    // =========================================================
    // CREATE SETTING
    // =========================================================

    @PostMapping
    public ResponseEntity<SystemSettingResponse>
    createSetting(
            @Valid @RequestBody SystemSettingRequest request) {

        return ResponseEntity.ok(
                systemSettingService.createSetting(request)
        );
    }

    // =========================================================
    // UPDATE SETTING
    // =========================================================

    @PutMapping("/{key}")
    public ResponseEntity<SystemSettingResponse>
    updateSetting(
            @PathVariable String key,
            @Valid @RequestBody SystemSettingRequest request) {

        return ResponseEntity.ok(
                systemSettingService.updateSetting(
                        key,
                        request
                )
        );
    }

    // =========================================================
    // DELETE SETTING
    // =========================================================

    @DeleteMapping("/{key}")
    public ResponseEntity<String>
    deleteSetting(
            @PathVariable String key) {

        systemSettingService.deleteSetting(key);

        return ResponseEntity.ok(
                "System setting deleted successfully."
        );
    }
}
