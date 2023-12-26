package ru.gb.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyDataBase {

    public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS schoolDB;";
    public static final String USE_DB = "USE schoolDB;";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS courses (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), duration INT);";

    public void createDatabase(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_DATABASE)) {
            statement.execute();
        }
    }

    public void useDatabase(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(USE_DB)) {
            statement.execute();
        }
    }

    public void createTable(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();
        }
    }

}
