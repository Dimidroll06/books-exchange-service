package ru.dmitry.books.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.dmitry.books.model.GenreEntity;

/**
 * 
 * @author dmitry
 */
@Repository
public interface GenreRepository extends  JpaRepository<GenreEntity, Long>{
    List<GenreEntity> findByName(String name);
}
