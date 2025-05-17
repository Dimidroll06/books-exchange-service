package com.dmitry.books.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.dmitry.books.dto.ExchangeStatusStatDTO;
import com.dmitry.books.dto.GenreCreateDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.model.GenreEntity;
import com.dmitry.books.repository.BookRepository;
import com.dmitry.books.repository.ExchangeRepository;
import com.dmitry.books.repository.GenreRepository;

class AdminServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ExchangeRepository exchangeRepository;
    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooks_ReturnsList() {
        BookEntity book1 = new BookEntity();
        BookEntity book2 = new BookEntity();
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<BookEntity> result = adminService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void getAllBooks_EmptyList() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<BookEntity> result = adminService.getAllBooks();

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    @Test
    void getExchangeStats_ReturnsStats() {
        Object[] stat1 = new Object[]{0, 5L};
        Object[] stat2 = new Object[]{1, 2L};
        when(exchangeRepository.countExchangesByStatus()).thenReturn(List.of(stat1, stat2));

        List<ExchangeStatusStatDTO> stats = adminService.getExchangeStats();

        assertEquals(2, stats.size());
        assertEquals(0, stats.get(0).getStatus());
        assertEquals(5L, stats.get(0).getCount());
        assertEquals(1, stats.get(1).getStatus());
        assertEquals(2L, stats.get(1).getCount());
        verify(exchangeRepository).countExchangesByStatus();
    }

    @Test
    void getExchangeStats_Empty() {
        when(exchangeRepository.countExchangesByStatus()).thenReturn(List.of());

        List<ExchangeStatusStatDTO> stats = adminService.getExchangeStats();

        assertNotNull(stats);
        assertTrue(stats.isEmpty());
        verify(exchangeRepository).countExchangesByStatus();
    }

    @Test
    void addGenre_SavesGenre() {
        GenreCreateDTO dto = new GenreCreateDTO();
        dto.setName("TestGenre");

        adminService.addGenre(dto);

        ArgumentCaptor<GenreEntity> captor = ArgumentCaptor.forClass(GenreEntity.class);
        verify(genreRepository).save(captor.capture());
        GenreEntity saved = captor.getValue();
        assertEquals("TestGenre", saved.getName());
    }
}