package ru.dmitry.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.dmitry.books.model.BookEntity;

/**
 *
 * @author dmitry
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    @Query("""
            SELECT b FROM BookEntity b
            WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
            AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
            AND (:genreId IS NULL OR b.genre.id = :genreId)
            """)
    Page<BookEntity> findByFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("genreId") Long genreId,
            Pageable pageable);
}
