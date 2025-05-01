package ru.dmitry.books.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.BookCopyDTO;
import ru.dmitry.books.dto.BookCopyResponseDTO;
import ru.dmitry.books.model.BookCopyEntity;
import ru.dmitry.books.model.BookEntity;
import ru.dmitry.books.repository.BookCopyRepository;
import ru.dmitry.books.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyResponseDTO createBookCopy(BookCopyDTO dto) {
        BookEntity book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setBook(book);
        bookCopy.setOwnerId(dto.ownerId());

        BookCopyEntity savedCopy = bookCopyRepository.save(bookCopy);
        return toDto(savedCopy);
    }

    public Page<BookCopyResponseDTO> getCopiesByBookId(Long bookId, Pageable pageable) {
        Page<BookCopyEntity> copies = bookCopyRepository.findByBookId(bookId, pageable);
        return copies.map(this::toDto);
    }

    public Page<BookCopyResponseDTO> getCopiesByOwnerId(Long ownerId, Pageable pageable) {
        Page<BookCopyEntity> copies = bookCopyRepository.findByOwnerId(ownerId, pageable);
        return copies.map(this::toDto);
    }

    private BookCopyResponseDTO toDto(BookCopyEntity bookCopy) {
        return new BookCopyResponseDTO(
                bookCopy.getId(),
                bookCopy.getBook().getId(),
                bookCopy.getBook().getTitle(),
                bookCopy.getOwnerId());
    }
}