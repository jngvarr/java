package ru.gb.lecture;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Connector {
    final StandardServiceRegistry registry;
    SessionFactory sessionFactory;

    public Connector() {
        registry = new StandardServiceRegistryBuilder().
                configure().
                build();
        sessionFactory = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}

