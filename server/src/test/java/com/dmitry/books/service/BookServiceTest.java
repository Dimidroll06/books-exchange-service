package com.dmitry.books.service;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dmitry.books.dto.BookRequestDTO;
import com.dmitry.books.dto.BookResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.model.GenreEntity;
import com.dmitry.books.repository.BookRepository;
import com.dmitry.books.repository.GenreRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private BookService bookService;

    private BookEntity bookEntity;
    private GenreEntity genreEntity;

    @BeforeEach
    void setUp() {
        bookEntity = new BookEntity();
        bookEntity.setId(1L);
        bookEntity.setTitle("Test Book");
        bookEntity.setAuthor("John Doe");
        bookEntity.setDescription("Test Description");
        bookEntity.setGenreId(1L);

        genreEntity = new GenreEntity();
        genreEntity.setId(1L);
        genreEntity.setName("Fiction");
    }

    @Test
    void testGetBooksByFilter() {
        Page<BookEntity> page = new PageImpl<>(Collections.singletonList(bookEntity));
        Mockito.when(bookRepository.findByFilters(any(), any(), any(), any())).thenReturn(page);
        Mockito.when(genreRepository.findById(1L)).thenReturn(Optional.of(genreEntity));

        Pageable pageable = PageRequest.of(0, 10);
        PageDTO<BookResponseDTO> result = bookService.getBooksByFilter("John Doe", 1L, "Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }

    @Test
    void testGetBookById() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(genreRepository.findById(1L)).thenReturn(Optional.of(genreEntity));

        Optional<BookResponseDTO> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
        assertEquals("Fiction", result.get().getGenre());
    }

    @Test
    void testGetBookById_NotFound() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<BookResponseDTO> result = bookService.getBookById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateBook() {
        BookRequestDTO bookRequest = new BookRequestDTO();
        bookRequest.setTitle("New Book");
        bookRequest.setAuthor("Jane Doe");
        bookRequest.setDescription("New Description");
        bookRequest.setGenreId(1L);

        Mockito.when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        assertDoesNotThrow(() -> bookService.createBook(bookRequest));
        Mockito.verify(bookRepository, Mockito.times(1)).save(any(BookEntity.class));
    }

    @Test
    void testUpdateBook() {
        BookRequestDTO updatedBook = new BookRequestDTO();
        updatedBook.setTitle("Updated Book");
        updatedBook.setAuthor("Jane Doe");
        updatedBook.setDescription("Updated Description");
        updatedBook.setGenreId(1L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        assertDoesNotThrow(() -> bookService.updateBook(1L, updatedBook));
        assertEquals("Updated Book", bookEntity.getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).save(bookEntity);
    }

    @Test
    void testUpdateBook_NotFound() {
        BookRequestDTO updatedBook = new BookRequestDTO();
        updatedBook.setTitle("Updated Book");

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(1L, updatedBook));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testDeleteBook() {
        Mockito.doNothing().when(bookRepository).deleteById(1L);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(1L);
    }
}