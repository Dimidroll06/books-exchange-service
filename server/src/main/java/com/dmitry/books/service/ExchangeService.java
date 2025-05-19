package com.dmitry.books.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dmitry.books.dto.BookCopyResponseDTO;
import com.dmitry.books.dto.ExchangeRequestDTO;
import com.dmitry.books.dto.ExchangeResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.dto.UserDTO;
import com.dmitry.books.model.BookCopyEntity;
import com.dmitry.books.model.ExchangeEntity;
import com.dmitry.books.repository.BookCopyRepository;
import com.dmitry.books.repository.ExchangeRepository;
import com.dmitry.books.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExchangeService {

    @Autowired
    private final ExchangeRepository exchangeRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BookCopyRepository bookCopyRepository;

    public PageDTO<ExchangeResponseDTO> getExchangeBySenderId(Long fromUserId, Pageable pageable) {
        Page<ExchangeEntity> page = exchangeRepository.findBySenderId(fromUserId, pageable);
        
        PageDTO<ExchangeResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::toDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }

    public PageDTO<ExchangeResponseDTO> getExchangeByGetterId(Long toUserId, Pageable pageable) {
        Page<ExchangeEntity> page = exchangeRepository.findByGetterId(toUserId, pageable);

        PageDTO<ExchangeResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::toDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }

    public PageDTO<ExchangeResponseDTO> getExchangeByUserId(Long userId, Pageable pageable) {
        Page<ExchangeEntity> page = exchangeRepository.findByUserId(userId, pageable);

        PageDTO<ExchangeResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::toDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }

    public Optional<ExchangeResponseDTO> getExchangeById(Long id) {
        return exchangeRepository.findById(id)
            .map(this::toDto);
    }

    public Boolean isBookCopyInExchange(Long bookCopyId) {
        return exchangeRepository.isCurrentlyInExchange(bookCopyId);
    }

    public void createExchange(ExchangeRequestDTO exchange, Long userId) {
        if (exchangeRepository.isCurrentlyInExchange(exchange.getBookCopyId())) {
            throw new EntityExistsException("This book copy currently in exchange");
        }
        ExchangeEntity exchangeEntity = toEntity(exchange, userId);
        exchangeRepository.save(exchangeEntity);
    }

    public void rejectExchange(Long exchangeId, Long userId) {
        ExchangeEntity exchangeEntity = exchangeRepository.findById(exchangeId)
                .orElseThrow(
                    () -> new EntityNotFoundException("Exchange not found")
                );
    
        if (!Objects.equals(exchangeEntity.getFromUserId(), userId) && !Objects.equals(exchangeEntity.getToUserId(), userId)) {
            throw new SecurityException("You can't change status of this exchange");
        }

        exchangeEntity.setStatus(-1);
        exchangeRepository.save(exchangeEntity);
    }

    public void sendExchange(Long exchangeId, Long userId) {
        ExchangeEntity exchangeEntity = exchangeRepository.findById(exchangeId)
                .orElseThrow(
                    () -> new EntityNotFoundException("Exchange not found")
                );
    
        if (exchangeEntity.getStatus() != 0) {
            throw new IllegalArgumentException("Status is incorrect");
        }

        if (!Objects.equals(exchangeEntity.getFromUserId(), userId)) {
            throw new SecurityException("You can't change status of this exchange");
        }

        exchangeEntity.setStatus(1);
        exchangeRepository.save(exchangeEntity);
    }

    public void acceptExchange(Long exchangeId, Long userId) {
        ExchangeEntity exchangeEntity = exchangeRepository.findById(exchangeId)
                .orElseThrow(
                    () -> new EntityNotFoundException("Exchange not found")
                );
    
        if (exchangeEntity.getStatus() != 1) {
            throw new IllegalArgumentException("Status is incorrect");
        }
        
        if (!Objects.equals(exchangeEntity.getToUserId(), userId)) {
            throw new SecurityException("You can't change status of this exchange");
        }
        BookCopyEntity book = bookCopyRepository.findById(exchangeEntity.getBookCopyId()).orElseThrow(
            () -> new EntityNotFoundException("BookCopy not found")
        );
        book.setOwnerId(exchangeEntity.getToUserId());
        bookCopyRepository.save(book);
        exchangeEntity.setStatus(2);
        exchangeRepository.save(exchangeEntity);
    }

    

    // ДТОшечки, мм, ура

    private ExchangeResponseDTO toDto(ExchangeEntity exchange) {
        ExchangeResponseDTO dto = new ExchangeResponseDTO();
        dto.setId(exchange.getId());
        dto.setLocation(exchange.getLocation());
        switch (exchange.getStatus()) {
            case -1 -> dto.setStatus("rejected");
            case 1 -> dto.setStatus("sended");
            case 2 -> dto.setStatus("accepted");
            default -> dto.setStatus("created");
        }

        BookCopyEntity bookCopy = bookCopyRepository.findById(exchange.getBookCopyId()).orElse(null);
        BookCopyResponseDTO bookCopyDto = new BookCopyResponseDTO();
        bookCopyDto.setId(exchange.getBookCopyId());
        
        if (bookCopy != null) {
            bookCopyDto.setBookId(bookCopy.getId());
            bookCopyDto.setOwnerId(bookCopy.getOwnerId());
        }
        dto.setBookCopy(bookCopyDto);

        UserDTO fromUserDTO = userRepository.getUserById(exchange.getFromUserId());
        UserDTO toUserDTO = userRepository.getUserById(exchange.getToUserId());
        
        dto.setFrom(fromUserDTO);
        dto.setTo(toUserDTO);

        return dto;
    }    

    private ExchangeEntity toEntity(ExchangeRequestDTO dto, Long fromUserId) {
        ExchangeEntity entity = new ExchangeEntity();

        BookCopyEntity bookCopy = bookCopyRepository.findById(dto.getBookCopyId())
            .orElseThrow(
                () -> new EntityNotFoundException("Book copy not found")
            );
        
        entity.setBookCopyId(dto.getBookCopyId());
        entity.setFromUserId(fromUserId);
        entity.setToUserId(bookCopy.getOwnerId());
        entity.setLocation(dto.getLocation());
        entity.setStatus(0);

        return entity;
    }
}
