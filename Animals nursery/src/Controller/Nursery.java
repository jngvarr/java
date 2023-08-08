package Controller;

import Controller.AnimalController;
import Model.Animals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;


public class Nursery {
//    public AnimalController roller;
    private Statement sqlStatement;
    private ResultSet resultSet;
    private String SQLQuery;
    Animals animal;

    public List<Animals> getAll() throws SQLException, ClassNotFoundException, IOException {
        List<Animals> list = new ArrayList<>();
        AnimalController controller = new AnimalController();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connect = getConnection()) {
                {
                    sqlStatement = connect.createStatement();
                    SQLQuery = "SELECT * FROM all_animals ORDER BY Id";
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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static Connection getConnection() throws SQLException {

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/Sources/connection.properties")) {

            properties.load(fileInputStream);
            String url = properties.getProperty("url");
            String username = properties.getProperty("user");
            String password = properties.getProperty("password");
//.setAllowPublicKeyRetrieval(true);
            return DriverManager.getConnection(url, username, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
