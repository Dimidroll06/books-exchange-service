package com.dmitry.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmitry.books.model.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

        @Query("SELECT b FROM BookEntity b " +
                        "WHERE (:author IS NULL OR b.author = :author) " +
                        "AND (:genreId IS NULL OR b.genreId = :genreId) " +
                        "AND (:title IS NULL OR b.title LIKE %:title%)")
        Page<BookEntity> findByFilters(@Param("author") String author,
                        @Param("genreId") Long genreId,
                        @Param("title") String title,
                        Pageable pageable);
}
