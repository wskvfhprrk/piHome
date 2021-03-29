package com.example.demo;

import java.sql.*;

public class SQLiteJDBC {
    public static void main(String args[]) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}