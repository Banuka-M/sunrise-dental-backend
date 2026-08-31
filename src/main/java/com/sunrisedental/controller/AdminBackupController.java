package com.sunrisedental.controller;

import com.sunrisedental.service.DatabaseBackupService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/backup")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminBackupController {

    private final DatabaseBackupService databaseBackupService;

    public AdminBackupController(
            DatabaseBackupService databaseBackupService) {

        this.databaseBackupService =
                databaseBackupService;
    }

    // =========================================================
    // CREATE DATABASE BACKUP
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createBackup() {

        try {

            String backupFile =
                    databaseBackupService.createBackup();

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Database backup created successfully.",

                            "file",
                            backupFile
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Database backup failed.",

                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
}
