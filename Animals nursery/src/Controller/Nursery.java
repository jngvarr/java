package Controller;

import Model.Animals;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Nursery {
    private Statement sqlStatement;
    private ResultSet resultSet;
    private String SQLQuery;

    public List<Animals> getAll() throws IOException {
        List<Animals> list = new ArrayList<>();
        Animals animal;
        AnimalController controller = new AnimalController();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = "select * from all_animals;";
                    resultSet = sqlStatement.executeQuery(SQLQuery);

                    while (resultSet.next()) {
                        String id = resultSet.getString(1);
                        String name = resultSet.getString(2);
                        String day_of_birth = resultSet.getString(3);
                        String commands = resultSet.getString(4);
                        String type = resultSet.getString(5);
                        animal = controller.createAnimal(id, name, day_of_birth, commands, type);
                        list.add(animal);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void addAnimal(Animals animal) {
        AnimalController controller = new AnimalController();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = "insert all_animals (Name, day_of_birth, commands, friends_name) \n" +
                            String.format("Values('%s', '%s', '%s' , '%s');", animal.getName(), animal.getDayOfBirth(), animal.getCommands(), animal.getType());
                    sqlStatement.executeUpdate(SQLQuery);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateData(String[] newData, String id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = String.format("Update all_animals SET Name = '%s', day_of_birth = '%s', commands = '%s', friends_name  = '%s' where id = '%s'",
                            newData[0], newData[1], newData[2], newData[3], id);
                    sqlStatement.executeUpdate(SQLQuery);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCommands(String id) {
        String commands = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = String.format("Select commands From all_animals WHERE id = '%s'", id);
                    resultSet = sqlStatement.executeQuery(SQLQuery);
                    while (resultSet.next()) {
                        commands += resultSet.getString(1) + " ";
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
        }
        return commands;
    }

    public String getAnimal(String id) {
        String animal = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = String.format("Select * From all_animals WHERE id = '%s';", id);
                    resultSet = sqlStatement.executeQuery(SQLQuery);
                    while (resultSet.next()) {
                        String ID = resultSet.getString(1);
                        String name = resultSet.getString(2);
                        String day_of_birth = resultSet.getString(3);
                        String commands = resultSet.getString(4);
                        String type = resultSet.getString(5);

                    animal = String.format("%s; %s; %s; %s; %s", ID, name, day_of_birth, commands, type);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
        }
        return animal;
    }

    public void deleteAnimal(String id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = String.format("DELETE from all_animals where id = '%s';", id);
                    sqlStatement.executeUpdate(SQLQuery);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void trainAnimal(String commands, String id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = String.format("UPDATE all_animals SET commands = '%s' where id = '%s';", commands, id);
                    sqlStatement.executeUpdate(SQLQuery);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/Sources/connection.properties")) {
            properties.load(fileInputStream);
            String url = properties.getProperty("url");
            String username = properties.getProperty("user");
            String password = properties.getProperty("password");
            return DriverManager.getConnection(url, username, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
