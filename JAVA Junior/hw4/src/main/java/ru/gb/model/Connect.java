package ru.gb.model;

import java.sql.*;

public class Connect {
    private static final String URL = "jdbc:mysql://45.153.68.226:3307";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static Connection con() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD); {
            return con;
        }
    }
}
