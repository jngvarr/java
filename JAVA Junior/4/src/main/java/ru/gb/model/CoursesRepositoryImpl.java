package ru.gb.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.gb.Course;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class CoursesRepositoryImpl implements CoursesRepository{
    @Override
    public void add(Course item) {
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

            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Course item) {

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
