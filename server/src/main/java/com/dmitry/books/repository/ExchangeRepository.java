package com.dmitry.books.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmitry.books.model.ExchangeEntity;

public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long>{
    
    @Query("SELECT ex FROM ExchangeEntity ex WHERE ex.fromUserId = :fromUser")
    Page<ExchangeEntity> findBySenderId(@Param("fromUser") Long fromUser, Pageable pageable);

    @Query("SELECT ex FROM ExchangeEntity ex WHERE ex.toUserId = :toUser")
    Page<ExchangeEntity> findByGetterId(@Param("toUser") Long toUser, Pageable pageable);

    @Query("SELECT ex FROM ExchangeEntity ex " +
            "WHERE (ex.fromUserId = :userId) " +
            "OR (ex.toUserId = :toUser)")
    Page<ExchangeEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(ex) > 0 THEN true ELSE false END " +
           "FROM ExchangeEntity ex " +
           "WHERE ex.bookCopyId = :bookCopyId " +
           "AND ex.status NOT IN (-1, 2)")
    boolean isCurrentlyInExchange(@Param("bookCopyId") Long bookCopyId);
}
