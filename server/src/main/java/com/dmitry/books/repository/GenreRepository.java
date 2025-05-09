package com.dmitry.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmitry.books.model.GenreEntity;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {

}
