package ru.gb;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.gb.model.Connect;
import ru.gb.model.MyDataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection = Connect.con();
        MyDataBase dataBase = new MyDataBase();

        // создание бд
        try {
            dataBase.createDatabase(connection);
            dataBase.useDatabase(connection);
            dataBase.createTable(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try(SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Course.class)
                .buildSessionFactory()){

            // Создание сессии
            Session session = sessionFactory.getCurrentSession();

            // Начало транзакции
            session.beginTransaction();

            // Создание объекта
            Course course = Course.create();
            session.save(course);
            System.out.println("Object course successfully saved");

            // Чтение объекта из базы данных
            Course retrievedCourse = session.get(Course.class, course.getId());
            System.out.println("Object student successfully retrieved");
            System.out.println("Retrieved course object: " + retrievedCourse);

            // Обновление объекта

            retrievedCourse.update(course);
            session.update(retrievedCourse);
            System.out.println("Object course successfully updated");

            //Course courseToDelete = session.get(Course.class, 5);
            session.delete(retrievedCourse);
            //session.delete(courseToDelete);
            System.out.println("Object student delete successfully");
            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
