package com.dmitry.books.service;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dmitry.books.dto.BookCopyRequestDTO;
import com.dmitry.books.dto.BookCopyResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.model.BookCopyEntity;
import com.dmitry.books.repository.BookCopyRepository;

import jakarta.persistence.EntityNotFoundException;

class BookCopyServiceTest {

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BookCopyService bookCopyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookCopiesByOwnerId() {
        Pageable pageable = PageRequest.of(0, 10);
        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setId(1L);
        bookCopy.setOwnerId(1L);
        bookCopy.setBookId(1L);

        Page<BookCopyEntity> page = new PageImpl<>(Collections.singletonList(bookCopy), pageable, 1);
        when(bookCopyRepository.findByOwnerId(1L, pageable)).thenReturn(page);

        PageDTO<BookCopyResponseDTO> result = bookCopyService.getBookCopiesByOwnerId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(bookCopyRepository, times(1)).findByOwnerId(1L, pageable);
    }

    @Test
    void testGetBookCopiesByOwnerId_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookCopyEntity> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(bookCopyRepository.findByOwnerId(1L, pageable)).thenReturn(page);

        PageDTO<BookCopyResponseDTO> result = bookCopyService.getBookCopiesByOwnerId(1L, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(bookCopyRepository, times(1)).findByOwnerId(1L, pageable);
    }

    @Test
    void testGetBookCopyById() {
        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setId(1L);
        bookCopy.setOwnerId(1L);
        bookCopy.setBookId(1L);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

        Optional<BookCopyResponseDTO> result = bookCopyService.getBookCopyById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(bookCopyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookCopyById_NotFound() {
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<BookCopyResponseDTO> result = bookCopyService.getBookCopyById(1L);

        assertFalse(result.isPresent());
        verify(bookCopyRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateBookCopy() {
        BookCopyRequestDTO dto = new BookCopyRequestDTO();
        dto.setBookId(1L);

        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setOwnerId(1L);
        bookCopy.setBookId(1L);

        when(bookCopyRepository.save(any(BookCopyEntity.class))).thenReturn(bookCopy);

        bookCopyService.createBookCopy(1L, dto);

        verify(bookCopyRepository, times(1)).save(any(BookCopyEntity.class));
    }

    @Test
    void testSetNewOwner() {
        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setId(1L);
        bookCopy.setOwnerId(1L);

        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        when(bookCopyRepository.save(any(BookCopyEntity.class))).thenReturn(bookCopy);

        bookCopyService.setNewOwner(1L, 2L);

        assertEquals(2L, bookCopy.getOwnerId());
        verify(bookCopyRepository, times(1)).findById(1L);
        verify(bookCopyRepository, times(1)).save(bookCopy);
    }

    @Test
    void testSetNewOwner_NotFound() {
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookCopyService.setNewOwner(1L, 2L));
        assertEquals("Book copy not found", exception.getMessage());

        verify(bookCopyRepository, times(1)).findById(1L);
        verify(bookCopyRepository, never()).save(any(BookCopyEntity.class));
    }

    @Test
    void testDeleteBookCopy() {
        doNothing().when(bookCopyRepository).deleteById(1L);

        bookCopyService.deleteBookCopy(1L);

        verify(bookCopyRepository, times(1)).deleteById(1L);
    }
}