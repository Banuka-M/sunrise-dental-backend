package com.sunrisedental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupService {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    /*
     * Location where backup files are stored.
     *
     * Default:
     * backups/
     */
    @Value("${app.backup.directory:backups}")
    private String backupDirectory;

    // =========================================================
    // CREATE BACKUP
    // =========================================================

    public String createBackup() throws Exception {

        DatabaseInfo databaseInfo =
                parseDatabaseUrl(databaseUrl);

        Path backupPath =
                Path.of(backupDirectory);

        Files.createDirectories(
                backupPath
        );

        String timestamp =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd_HHmmss"
                                )
                        );

        String fileName =
                "sunrise_dental_backup_"
                        + timestamp
                        + ".sql";

        Path outputFile =
                backupPath.resolve(fileName);

        /*
         * mysqldump command.
         *
         * --single-transaction allows a consistent
         * backup for InnoDB without locking tables.
         */
        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        "mysqldump",
                        "--host=" + databaseInfo.host,
                        "--port=" + databaseInfo.port,
                        "--user=" + databaseUsername,
                        "--password=" + databasePassword,
                        "--single-transaction",
                        "--routines",
                        "--triggers",
                        "--result-file=" + outputFile.toAbsolutePath(),
                        databaseInfo.database
                );

        processBuilder.redirectErrorStream(true);

        Process process =
                processBuilder.start();

        StringBuilder output =
                new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     process.getInputStream(),
                                     StandardCharsets.UTF_8
                             ))) {

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode =
                process.waitFor();

        if (exitCode != 0) {

            Files.deleteIfExists(outputFile);

            throw new IllegalStateException(
                    "Database backup failed. "
                            + output
            );
        }

        if (!Files.exists(outputFile)
                || Files.size(outputFile) == 0) {

            throw new IllegalStateException(
                    "Database backup file was not created."
            );
        }

        return outputFile.toAbsolutePath().toString();
    }

    // =========================================================
    // PARSE DATABASE URL
    // =========================================================

    private DatabaseInfo parseDatabaseUrl(
            String url) {

        /*
         * Example:
         *
         * jdbc:mysql://localhost:3306/sunrise_dental
         *
         * We extract:
         *
         * host     = localhost
         * port     = 3306
         * database = sunrise_dental
         */

        String prefix =
                "jdbc:mysql://";

        if (!url.startsWith(prefix)) {

            throw new IllegalArgumentException(
                    "Only MySQL datasource URLs are supported."
            );
        }

        String remaining =
                url.substring(prefix.length());

        int slashIndex =
                remaining.indexOf('/');

        if (slashIndex == -1) {

            throw new IllegalArgumentException(
                    "Invalid MySQL datasource URL."
            );
        }

        String hostPort =
                remaining.substring(
                        0,
                        slashIndex
                );

        String databasePart =
                remaining.substring(
                        slashIndex + 1
                );

        /*
         * Remove query parameters.
         *
         * Example:
         *
         * sunrise_dental?useSSL=false
         */

        int questionMark =
                databasePart.indexOf('?');

        if (questionMark != -1) {

            databasePart =
                    databasePart.substring(
                            0,
                            questionMark
                    );
        }

        String host = "localhost";
        String port = "3306";

        if (hostPort.contains(":")) {

            String[] parts =
                    hostPort.split(":", 2);

            host = parts[0];
            port = parts[1];

        } else {

            host = hostPort;
        }

        if (databasePart.isBlank()) {

            throw new IllegalArgumentException(
                    "Database name could not be determined."
            );
        }

        return new DatabaseInfo(
                host,
                port,
                databasePart
        );
    }

    // =========================================================
    // INTERNAL DATABASE INFO
    // =========================================================

    private static class DatabaseInfo {

        private final String host;
        private final String port;
        private final String database;

        private DatabaseInfo(
                String host,
                String port,
                String database) {

            this.host = host;
            this.port = port;
            this.database = database;
        }
    }
}
