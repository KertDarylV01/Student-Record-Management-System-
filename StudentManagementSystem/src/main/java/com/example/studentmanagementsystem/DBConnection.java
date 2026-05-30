package com.example.studentmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection connect() {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/studentdb",
                    "postgres",
                    "your_password"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

