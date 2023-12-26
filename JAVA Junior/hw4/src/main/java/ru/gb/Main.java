package ru.gb;
import java.sql.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.gb.model.Course;
import ru.gb.model.MyDataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverManager.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        MyDataBase dataBase = new MyDataBase();

        final String URL = "jdbc:mysql://45.153.68.226:3306";
        final String USER = "root";
        final String PASSWORD = "ihu378";

        // Подключение к базе данных
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            dataBase.createDatabase(con);
            dataBase.useDatabase(con);
            dataBase.createTable(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        try (SessionFactory sessionFactory = new Configuration()
//                .configure("hibernate.cfg.xml")
//                .addAnnotatedClass(Course.class)
//                .buildSessionFactory()) {
//
//            dataBase.createDatabase((Connection) sessionFactory);
//
//            // Создание сессии
//            Session session = sessionFactory.getCurrentSession();
//
//            // Начало транзакции
//            session.beginTransaction();
//
//            // Создание объекта
//            Course course = Course.create();
//            session.save(course);
//            System.out.println("Object course save successfully");

//            // Чтение объекта из базы данных
//            Student retrievedStudent = session.get(Student.class, student.getId());
//            System.out.println("Object student retrieved successfully");
//            System.out.println("Retrieved student object: " + retrievedStudent);
//
//            // Обновление объекта
//            retrievedStudent.updateName();
//            retrievedStudent.updateAge();
//            session.update(retrievedStudent);
//            System.out.println("Object student update successfully");
//
//
//            session.delete(retrievedStudent);
//            System.out.println("Object student delete successfully");


//            session.getTransaction().commit();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
