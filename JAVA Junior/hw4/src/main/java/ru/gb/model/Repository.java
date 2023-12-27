package ru.gb.model;

import java.util.Collection;

public interface Repository<T, TId> {

    void add(T item);
    void update(T item);
    void delete(T item);
    T getById(TId id);
    Collection<T> getAll();

}
