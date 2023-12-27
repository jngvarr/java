package ru.gb;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
//        Connection connection = Connect.con();
//        MyDataBase dataBase = new MyDataBase();
//
//        // создание бд
//        try {
//            dataBase.createDatabase(connection);
//            dataBase.useDatabase(connection);
//            dataBase.createTable(connection);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

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
            System.out.println("Object course save successfully");

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
//
//
//            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
