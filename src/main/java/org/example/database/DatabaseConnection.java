package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://164.92.253.36:3306/competitive_gaming_management_system?allowPublicKeyRetrieval=true&useSSL=false";

    private static final String USER = "12220425_project_db";
    private static final String PASSWORD = "60406559";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}