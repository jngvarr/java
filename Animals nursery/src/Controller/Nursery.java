package Controller;

import Model.Animal;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Nursery {
    private static final String ADD_ANIMAL_QUERY = "insert all_animals (Name, day_of_birth, commands, friends_name) VALUES (?,?,?,?)";
    private static final String GET_ALL_QUERY = "select * from all_animals";
    private static final String UPDATE_DATA_QUERY = "Update all_animals SET Name = ?, day_of_birth = ?, commands = ?, friends_name  = ? where id = ?";
    private static final String SELECT_COMMANDS_QUERY = "Select commands From all_animals WHERE id = ?";
    private static final String GET_ANIMAL_QUERY = "Select * From all_animals WHERE id = ?";
    private static final String DELETE_ANIMAL_QUERY = "DELETE from all_animals where id = ?";
    private static final String UPDATE_COMMAND_QUERY = "UPDATE all_animals SET commands = ? where id = ?";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Animal> getAll() throws SQLException {
        List<Animal> list = new ArrayList<>();
        Animal animal;
        try (Connection connect = getConnection();
             PreparedStatement sqlStatement = connect.prepareStatement(GET_ALL_QUERY);
             ResultSet resultSet = sqlStatement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String day_of_birth = resultSet.getString(3);
                String commands = resultSet.getString(4);
                String type = resultSet.getString(5);
                animal = AnimalController.createAnimal(id, name, day_of_birth, commands, type);
                list.add(animal);
            }
        }
        return list;
    }

    public void addAnimal(Animal animal) throws SQLException {
        try (Connection connect = getConnection();
             PreparedStatement statement = connect.prepareStatement(ADD_ANIMAL_QUERY)) {
            statement.setString(1, animal.getName());
            statement.setString(2, animal.getDayOfBirth());
            statement.setString(3, animal.getCommands());
            statement.setString(4, animal.getType());
            statement.executeUpdate();
        }
    }

    public void updateData(Animal newData, String id) throws SQLException {

        try (Connection connect = getConnection();
             PreparedStatement statement = connect.prepareStatement(UPDATE_DATA_QUERY)) {
            statement.setString(1, newData.getName());
            statement.setString(2, newData.getDayOfBirth());
            statement.setString(3, newData.getCommands());
            statement.setString(4, newData.getType());
            statement.setString(5, id);
            statement.executeUpdate();
        }
    }

    public String getCommands(String id) throws SQLException {
        StringBuilder commands = new StringBuilder();
        try (Connection connect = getConnection();
             PreparedStatement statement = connect.prepareStatement(SELECT_COMMANDS_QUERY)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    commands.append(rs.getString(1)).append(" ");
                }
                return commands.toString();
            }
        }
    }

    public Animal getAnimal(String id) throws SQLException {
        try (Connection connect = getConnection();
             PreparedStatement statement = connect.prepareStatement(GET_ANIMAL_QUERY)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                String selectedId = rs.getString(1);
                String name = rs.getString(2);
                String dayOfBirth = rs.getString(3);
                String commands = rs.getString(4);
                String type = rs.getString(5);

                return AnimalController.createAnimal(selectedId, name, dayOfBirth, commands, type);
            }
        }
    }

    public void deleteAnimal(String id) throws SQLException {
        try (Connection connect = getConnection();
             PreparedStatement statement = connect.prepareStatement(DELETE_ANIMAL_QUERY)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }

    public void trainAnimal(String commands, String id) throws SQLException {
        try (Connection connect = getConnection();
             PreparedStatement statement = connect.prepareStatement(UPDATE_COMMAND_QUERY)) {
            statement.setString(1, commands);
            statement.setString(2, id);
            statement.executeUpdate();
        }
    }


    private static Connection getConnection() throws SQLException {
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
