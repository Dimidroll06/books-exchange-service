package com.dmitry.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmitry.books.dto.BookRatingDTO;
import com.dmitry.books.model.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>{
    
    @Query("SELECT r FROM ReviewEntity r "+
                "WHERE (:bookId IS NULL OR r.bookId = :bookId) " +
                "AND (:userId IS NULL OR r.userId = :userId)")
    Page<ReviewEntity> findByFilters(@Param("bookId") Long bookId,
        @Param("userId") Long userId,
        Pageable pageable);

    @Query("SELECT NEW com.dmitry.books.dto.BookRatingDTO("+
                ":bookId, " +
                "COUNT(r), " +
                "COALESCE(AVG(r.rating), 0.0)) " +
                "FROM ReviewEntity r " +
                "WHERE (r.bookId = :bookId OR :bookId IS NULL) " +
                "GROUP BY r.bookId")
    BookRatingDTO findBookRatingInfo(@Param("bookId") Long bookId); 
}
