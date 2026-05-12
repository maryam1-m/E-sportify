package org.example.database;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            if (connection != null) {
                System.out.println("Database connected successfully!");
            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}