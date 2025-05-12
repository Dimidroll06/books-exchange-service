package com.dmitry.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmitry.books.model.BookCopyEntity;

public interface BookCopyRepository extends JpaRepository<BookCopyEntity, Long> {

    @Query("SELECT b FROM BookCopyEntity b WHERE b.ownerId = :ownerId")
    Page<BookCopyEntity> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

}
