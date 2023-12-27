package ru.gb.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Collection;
import java.util.List;

public class CoursesRepositoryImpl implements CoursesRepository {
    private final SessionFactory sessionFactory;

    public CoursesRepositoryImpl() {
        this.sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Course.class)
                .buildSessionFactory();
    }

    @Override
    public void add(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(course);
            transaction.commit();
            System.out.printf("The course %s was added.\n", course);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            course.update();
            session.update(course);
            transaction.commit();
            System.out.printf("The course %s was updated.\n", course);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Course course) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(course);
            transaction.commit();
            System.out.printf("The course %s was deleted.\n", course);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Course getById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Course course = session.get(Course.class, id);
            System.out.printf("The selected course is: %s.\n", course);
            return course;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Course> getAll() {
        Transaction transaction = null;
        List<Course> courses = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            courses = session.createQuery("FROM course", Course.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return courses;
    }
}
