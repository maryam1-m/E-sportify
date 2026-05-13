package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Matches IntelliJ Database data source {@code competitive_gaming_management_system@164.92.253.36}
 * (see {@code .idea/dataSources.xml} and {@code .idea/dataSources.local.xml}).
 * Password is not stored in those files; set {@link #PASSWORD} to the same value configured in IntelliJ.
 */
public final class DatabaseConnection {

    /** Same JDBC URL as IntelliJ (host, port, database path); extra params for Connector/J. */
    private static final String URL =
            "jdbc:mysql://164.92.253.36:3306/competitive_gaming_management_system"
                    + "?allowPublicKeyRetrieval=true"
                    + "&useSSL=false"
                    + "&serverTimezone=UTC"
                    + "&connectTimeout=10000"
                    + "&socketTimeout=10000";

    /** From IntelliJ {@code dataSources.local.xml} {@code user-name}. */
    private static final String USER = "12220425_project_db";

    /** Same password as in your IntelliJ data source (not committed to .idea XML). */
    private static final String PASSWORD = "60406559";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
