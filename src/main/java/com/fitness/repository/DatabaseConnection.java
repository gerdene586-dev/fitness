package com.fitness.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/fitness_membership?serverTimezone=UTC&connectTimeout=5000";

    private DatabaseConnection() {
    }

    public static Connection open() throws SQLException {
        Properties settings = loadSettings();
        String url = setting("MYSQL_URL", settings, DEFAULT_URL);
        String username = setting("MYSQL_USER", settings, "root");
        String password = setting("MYSQL_PASSWORD", settings, "");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Төслийн үндсэн хавтас дахь database.properties файлд MYSQL_PASSWORD-ийг тохируулна уу."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static Properties loadSettings() {
        Path settingsFile = Path.of("database.properties");
        Properties settings = new Properties();

        if (!Files.exists(settingsFile)) {
            return settings;
        }

        try (InputStream input = Files.newInputStream(settingsFile)) {
            settings.load(input);
            return settings;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "database.properties тохиргооны файлыг уншиж чадсангүй.",
                    e
            );
        }
    }

    private static String setting(
            String name,
            Properties settings,
            String defaultValue
    ) {
        String environmentValue = System.getenv(name);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return settings.getProperty(name, defaultValue).trim();
    }

    public static void verifyConnection() throws SQLException {
        try (Connection connection = open()) {
            if (!connection.isValid(5)) {
                throw new SQLException("MySQL холболтыг баталгаажуулж чадсангүй.");
            }
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT 1 FROM members LIMIT 0").close();
            }
        }
    }
}
