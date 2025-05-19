package com.dmitry.books.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmitry.books.dto.BookRequestDTO;
import com.dmitry.books.dto.BookResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.model.GenreEntity;
import com.dmitry.books.repository.BookRepository;
import com.dmitry.books.repository.GenreRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    public List<GenreEntity> getAllGenres() {
        return genreRepository.findAll();
    }

    public PageDTO<BookResponseDTO> getBooksByFilter(String author, Long genreId, String title, Pageable pageable) {
        Page<BookEntity> page = bookRepository.findByFilters(author, genreId, title, pageable);

        PageDTO<BookResponseDTO> result = new PageDTO<>();
        result.setContent(page.map(this::toDto).getContent());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setLast(page.isLast());

        return result;
    }

    public Optional<BookResponseDTO> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::toDto);
    }

    public void createBook(BookRequestDTO book) {
        BookEntity bookEntity = toEntity(book);
        bookRepository.save(bookEntity);
    }

    @Transactional
    public void updateBook(Long id, BookRequestDTO updatedBook) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setDescription(updatedBook.getDescription());
        book.setGenreId(updatedBook.getGenreId());

        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // DTO

    private BookResponseDTO toDto(BookEntity book) {
        BookResponseDTO dto = new BookResponseDTO();

        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());

        GenreEntity genre = genreRepository.findById(book.getGenreId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Genre not found"));
        dto.setGenre(genre.getName());

        return dto;
    }

    private BookEntity toEntity(BookRequestDTO dto) {

        BookEntity entity = new BookEntity();
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setDescription(dto.getDescription());
        entity.setGenreId(dto.getGenreId());

        return entity;
    }
}
