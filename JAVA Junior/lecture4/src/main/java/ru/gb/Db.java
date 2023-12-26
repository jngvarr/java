package ru.gb;

import java.sql.*;

public class Db {
    private static final String URL = "jdbc:mysql://45.153.68.226:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "ihu378";

    public static void con() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = con.createStatement();
            statement.execute("DROP SCHEMA `test`;");
            statement.execute("CREATE SCHEMA `test`;");
            statement.execute("CREATE TABLE `test`. `table`(id INT NOT NULL PRIMARY KEY auto_increment, firstname VARCHAR(45),lastname VARCHAR(45));");
            statement.execute("insert `test`.`table` (`firstname`,`lastname`)" +
                    "VALUES('Иванов', 'Иван');");
            statement.execute("insert `test`.`table` (`firstname`,`lastname`)" +
                    "VALUES('Петров', 'Петр');");
            ResultSet set = statement.executeQuery("select * from test.table;");
            while (set.next()) {
                System.out.println(set.getInt(1)+ " " + set.getString(3) + " " + set.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
