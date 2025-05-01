package ru.dmitry.books.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.dmitry.books.model.BookEntity;

/**
 *
 * @author dmitry
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    
    List<BookEntity> findByAuthor(String author);

    List<BookEntity> findByTitle(String title);
}
