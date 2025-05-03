package ru.dmitry.books.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.ExchangeCreateDTO;
import ru.dmitry.books.dto.ExchangeResponseDTO;
import ru.dmitry.books.dto.ExchangeUpdateStatusDTO;
import ru.dmitry.books.model.BookCopyEntity;
import ru.dmitry.books.model.ExchangeEntity;
import ru.dmitry.books.model.ExchangeEntity.ExchangeStatus;
import ru.dmitry.books.repository.BookCopyRepository;
import ru.dmitry.books.repository.ExchangeRepository;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final BookCopyRepository bookCopyRepository;

    @Transactional
    public ExchangeResponseDTO createExchange(Long fromUserId, ExchangeCreateDTO dto) {
        BookCopyEntity bookCopy = bookCopyRepository.findById(dto.getBookCopyId())
                .orElseThrow(() -> new EntityNotFoundException("BookCopy not found"));

        Long toUserId = bookCopy.getOwnerId();
        
        ExchangeEntity exchange = new ExchangeEntity();
        exchange.setBookCopy(bookCopy);
        exchange.setFromUserId(fromUserId);
        exchange.setToUserId(toUserId);
        exchange.setLocation(dto.getLocation());
        exchange.setStatus(ExchangeStatus.PENDING);
        exchange.setCreatedAt(Instant.now().toEpochMilli());
        exchange.setUpdatedAt(Instant.now().toEpochMilli());

        return toResponseDTO(exchangeRepository.save(exchange));
    }

    @Transactional
    public ExchangeResponseDTO updateExchangeStatus(Long exchangeId, Long userId, ExchangeUpdateStatusDTO dto) {
        ExchangeEntity exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new EntityNotFoundException("Exchange not found"));

        if (!exchange.getFromUserId().equals(userId) && !exchange.getToUserId().equals(userId)) {
            throw new SecurityException("Access denied");
        }

        ExchangeStatus newStatus = dto.getStatus();
        if (newStatus == ExchangeStatus.PROCEED && !exchange.getStatus().equals(ExchangeStatus.PENDING)) {
            throw new IllegalStateException("Exchange must be in PENDING state to proceed");
        }

        if (newStatus == ExchangeStatus.COMPLETED && !exchange.getStatus().equals(ExchangeStatus.PROCEED)) {
            throw new IllegalStateException("Exchange must be in PROCEED state to complete");
        }

        exchange.setStatus(newStatus);
        exchange.setUpdatedAt(Instant.now().toEpochMilli());

        return toResponseDTO(exchangeRepository.save(exchange));
    }

    public List<ExchangeResponseDTO> getUserExchanges(Long userId) {
        return exchangeRepository.findByFromUserIdOrToUserId(userId, userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private ExchangeResponseDTO toResponseDTO(ExchangeEntity exchange) {
        ExchangeResponseDTO dto = new ExchangeResponseDTO();
        dto.setId(exchange.getId());
        dto.setBookCopyId(exchange.getBookCopy().getId());
        dto.setFromUserId(exchange.getFromUserId());
        dto.setToUserId(exchange.getToUserId());
        dto.setStatus(exchange.getStatus());
        dto.setLocation(exchange.getLocation());
        dto.setCreatedAt(exchange.getCreatedAt());
        dto.setUpdatedAt(exchange.getUpdatedAt());
        return dto;
    }
}