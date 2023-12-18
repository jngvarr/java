package ru.gb;

import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Db2 {
    private static final String URL = "jdbc:mysql://45.153.68.226:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "ihu378";

    public static void con() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder(). //этот класс содержит механизмы для связи с сервером БД и менеджер передачи запроса
                configure().                                                          // для этого ему нужен файл конфигурации hibernate
                build();
        SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory(); // неизменяемый потокобезопасный объект
        Session session = sessionFactory.openSession();
        Magic magic = new Magic("Волшебная стрела", 10, 0, 0);
        session.beginTransaction();
        session.save(magic);
        session.getTransaction().commit();
        session.close();
    }
}

