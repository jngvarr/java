package ru.gb.model;

public class DataBase {

    public static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS schoolDB;";
    public static final String USE_DB = "USE studentsDB;";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS students (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), age INT);";

    private static void createDatabase(Connection connection) throws SQLException {
        String createDatabaseSQL = CREATE_DATABASE;
        try (PreparedStatement statement = connection.prepareStatement(createDatabaseSQL)) {
            statement.execute();
        }
    }

    private static void useDatabase(Connection connection) throws SQLException {
        String useDatabaseSQL = USE_DB;
        try (PreparedStatement statement = connection.prepareStatement(useDatabaseSQL)) {
            statement.execute();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableSQL = CREATE_TABLE;
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.execute();
        }
    }

}
