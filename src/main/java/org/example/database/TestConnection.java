package org.example.database;

import java.sql.Connection;

/**
 * Run this main before the JavaFX app to verify JDBC reaches MySQL with the same settings as {@link DatabaseConnection}.
 */
public final class TestConnection {

    private TestConnection() {
    }

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connected successfully from Java code!");
            }
        } catch (Exception e) {
            System.err.println("Connection failed.");
            e.printStackTrace();
        }
    }
}
