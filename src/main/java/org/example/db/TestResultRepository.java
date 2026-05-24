package org.example.db;

import org.example.config.TestConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class TestResultRepository {

    public static void init() {
        String sql = """
                CREATE TABLE IF NOT EXISTS test_results (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    test_name TEXT NOT NULL UNIQUE,
                    status TEXT NOT NULL,
                    execution_time DATETIME NOT NULL
                )
                """;
        try (Connection conn = DriverManager.getConnection(TestConfig.DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize SQLite database", e);
        }
    }

    public static void saveResult(String testName, String status, LocalDateTime executionTime) {
        String sql = """
                INSERT INTO test_results (test_name, status, execution_time) VALUES (?, ?, ?)
                ON CONFLICT(test_name) DO UPDATE SET
                    status = excluded.status,
                    execution_time = excluded.execution_time
                """;
        try (Connection conn = DriverManager.getConnection(TestConfig.DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, testName);
            ps.setString(2, status);
            ps.setString(3, executionTime.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save result for " + testName, e);
        }
    }
}
