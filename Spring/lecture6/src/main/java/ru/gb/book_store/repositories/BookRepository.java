package ru.gb.book_store.repositories;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.book_store.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
