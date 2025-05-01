package ru.dmitry.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.dmitry.books.model.GenreEntity;

/**
 * 
 * @author dmitry
 */
@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    GenreEntity findByName(String name);
}
