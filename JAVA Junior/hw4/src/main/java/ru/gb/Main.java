package ru.gb;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.gb.model.Course;

public class Main {
    public static void main(String[] args) {
        try (SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Course.class)
                .buildSessionFactory()) {

            // Создание сессии
            Session session = sessionFactory.getCurrentSession();

            // Начало транзакции
            session.beginTransaction();

            // Создание объекта
            Course student = Course.create();
            session.save(student);
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


            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
