package com.dmitry.books.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.BookCopyRequestDTO;
import com.dmitry.books.dto.BookCopyResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.model.BookCopyEntity;
import com.dmitry.books.repository.BookCopyRepository;
import com.dmitry.books.repository.BookRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookCopyService {
    
    @Autowired
    private BookCopyRepository bookCopyRepository;  
    
    @Autowired
    private BookRepository bookRepository;

    public PageDTO<BookCopyResponseDTO> getBookCopiesByOwnerId(Long ownerId, Pageable pageable) {
        Page<BookCopyEntity> page = bookCopyRepository.findByOwnerId(ownerId, pageable);

        PageDTO<BookCopyResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::toDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }

    public Optional<BookCopyResponseDTO> getBookCopyById(Long id) {
        return bookCopyRepository.findById(id)
            .map(this::toDto);
    }

    public PageDTO<BookCopyResponseDTO> getByOwnerId(Long id, Pageable pageable) {
        Page<BookCopyEntity> page = bookCopyRepository.findByOwnerId(id, pageable);

        PageDTO<BookCopyResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::toDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }


    public void createBookCopy(Long ownerId, BookCopyRequestDTO dto) {
        boolean bookExists = bookRepository.existsById(dto.getBookId());
        if (!bookExists) {
            throw new EntityNotFoundException("Book not found");
        }

        BookCopyEntity bookCopy = toEntity(ownerId, dto);
        bookCopyRepository.save(bookCopy);
    }

    @Transactional
    public void setNewOwner(Long id, Long ownerId) {
        BookCopyEntity bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book copy not found"));
        bookCopy.setOwnerId(ownerId);

        bookCopyRepository.save(bookCopy);
    }
    
    public void deleteBookCopy(Long id, AuthFilter.UserData userData) {
        BookCopyEntity bookCopy = bookCopyRepository.findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Book Copy not found")
            );

        if (!bookCopy.getOwnerId().equals(userData.getUserId())) {
            throw new SecurityException("Access denied: You are not the owner of this book copy");
        }

        bookCopyRepository.deleteById(id);
    }

    // DTO

    private BookCopyResponseDTO toDto(BookCopyEntity book) {
        BookCopyResponseDTO dto = new BookCopyResponseDTO();

        dto.setId(book.getId());
        dto.setBookId(book.getBookId());
        dto.setOwnerId(book.getOwnerId());

        return dto;
    }

    private BookCopyEntity toEntity(Long ownerId, BookCopyRequestDTO dto) {
        BookCopyEntity entity = new BookCopyEntity();

        entity.setOwnerId(ownerId);
        entity.setBookId(dto.getBookId());

        return entity;
    }
}
