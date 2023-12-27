package ru.gb.model;

import ru.gb.Course;
import ru.gb.model.CoursesRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Random;

public class CoursesRepositoryImpl implements CoursesRepository{
    private static Random rnd = new Random();
    @Override
    public void add(Course item) {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "password";

        // Подключение к базе данных
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            String insertDataSQL = "INSERT INTO students (name, age) VALUES (?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(insertDataSQL)) {
                statement.setString(1, item.getTitle());
                statement.setInt(2, item.getDuration());
                statement.executeUpdate();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static Course create() {
        String title = "Course #";
        return new Course(String.format("%s%d", title, rnd.nextInt(20)), rnd.nextInt(10));
    }

    public void update(Course course) {
        course.setDuration(rnd.nextInt(20));
    }

    @Override
    public void delete(Course item) {

    }

    @Override
    public Course getById(Integer integer) {
        return null;
    }

    @Override
    public Collection<Course> getAll() {
        return null;
    }
}
