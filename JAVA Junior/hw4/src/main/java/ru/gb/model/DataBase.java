package ru.gb.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBase {

    public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS schoolDB;";
    public static final String USE_DB = "USE schoolDB;";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS courses (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), duration INT);";

    private static void createDatabase(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_DATABASE)) {
            statement.execute();
        }
    }

    private static void useDatabase(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(USE_DB)) {
            statement.execute();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();
        }
    }

}
