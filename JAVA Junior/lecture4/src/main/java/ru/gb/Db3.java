package ru.gb.lecture;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class Db3 {
    private static final String URL = "jdbc:mysql://45.153.68.226:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "ihu378";

    public static void addData() {
        Connector connector = new Connector();
        try (Session session = connector.getSession()) {
            Magic magic = new Magic("Волшебная стрела", 10, 0, 6);
            session.beginTransaction();
            session.save(magic);
            magic = new Magic("Молния", 25, 0, 0);
            session.save(magic);
            magic = new Magic("Каменная кожа", 0, 6, 0);
            session.save(magic);
            magic = new Magic("Жажда крови", 0, 0, 6);
            session.save(magic);
            magic = new Magic("Жажда крови", 0, 0, 6);
            session.save(magic);
            magic = new Magic("Проклятие", 0, 0, -3);
            session.save(magic);
            magic = new Magic("Лечение", -30, 0, 0);
            session.save(magic);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readDB() {
        Connector connector = new Connector();
        // на чтение БД транзакция не создается (session.beginTransaction())
        try (Session session = connector.getSession()) {
            List<Magic> books = session.createQuery("FROM Magic", Magic.class).getResultList();
            books.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeDB() {
        Connector connector = new Connector();
        try (Session session = connector.getSession()) {
            String hql = "from magic where id = :id";
            Query<Magic> query = session.createQuery(hql, Magic.class);
            query.setParameter("id", 4);
            Magic magic = query.getSingleResult();
            System.out.println(magic);
            magic.setAttBonus(100);
            magic.setName("Ярость");
            session.beginTransaction();
            session.update(magic);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromDB() {
        Connector connector = new Connector();
        try (Session session = connector.getSession()) {
            Transaction t = session.beginTransaction();
            List<Magic> magics = session.createQuery("From Magic", Magic.class).getResultList();
            magics.forEach(m -> {
                session.delete(m);
            });
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
