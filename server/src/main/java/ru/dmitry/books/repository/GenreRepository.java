package ru.dmitry.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.dmitry.books.model.GenreEntity;

/**
 * 
 * @author dmitry
 */
public interface GenreRepository extends  JpaRepository<GenreEntity, Long>{
    
}
