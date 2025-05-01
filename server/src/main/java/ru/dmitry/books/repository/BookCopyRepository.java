package ru.dmitry.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.dmitry.books.model.BookCopyEntity;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopyEntity, Long> {

    Page<BookCopyEntity> findByBookId(Long bookId, Pageable pageable);

    Page<BookCopyEntity> findByOwnerId(Long ownerId, Pageable pageable);
}