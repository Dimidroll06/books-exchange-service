package ru.dmitry.books.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.dmitry.books.model.ExchangeEntity;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long> {
    List<ExchangeEntity> findByFromUserIdOrToUserId(Long fromUserId, Long toUserId);
}