package com.dmitry.books.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

class ExchangeServiceTest {

    @Mock
    private ExchangeRepository exchangeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeBySenderId() {
        Pageable pageable = PageRequest.of(0, 10);
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(1L);
        entity.setToUserId(2L);
        entity.setBookCopyId(10L);
        entity.setStatus(0);

        Page<ExchangeEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(exchangeRepository.findBySenderId(1L, pageable)).thenReturn(page);
        mockDtoDependencies(entity);

        PageDTO<ExchangeResponseDTO> result = exchangeService.getExchangeBySenderId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(exchangeRepository).findBySenderId(1L, pageable);
    }

    @Test
    void testGetExchangeBySenderId_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(exchangeRepository.findBySenderId(1L, pageable)).thenReturn(Page.empty(pageable));

        PageDTO<ExchangeResponseDTO> result = exchangeService.getExchangeBySenderId(1L, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void testGetExchangeByGetterId() {
        Pageable pageable = PageRequest.of(0, 10);
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(2L);
        entity.setFromUserId(1L);
        entity.setToUserId(2L);
        entity.setBookCopyId(10L);
        entity.setStatus(0);

        Page<ExchangeEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(exchangeRepository.findByGetterId(2L, pageable)).thenReturn(page);
        mockDtoDependencies(entity);

        PageDTO<ExchangeResponseDTO> result = exchangeService.getExchangeByGetterId(2L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2L, result.getContent().get(0).getId());
    }

    @Test
    void testGetExchangeByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(3L);
        entity.setFromUserId(1L);
        entity.setToUserId(3L);
        entity.setBookCopyId(11L);
        entity.setStatus(0);

        Page<ExchangeEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(exchangeRepository.findByUserId(3L, pageable)).thenReturn(page);
        mockDtoDependencies(entity);

        PageDTO<ExchangeResponseDTO> result = exchangeService.getExchangeByUserId(3L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(3L, result.getContent().get(0).getId());
    }

    @Test
    void testGetExchangeById_Found() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(4L);
        entity.setFromUserId(1L);
        entity.setToUserId(4L);
        entity.setBookCopyId(12L);
        entity.setStatus(0);

        when(exchangeRepository.findById(4L)).thenReturn(Optional.of(entity));
        mockDtoDependencies(entity);

        Optional<ExchangeResponseDTO> result = exchangeService.getExchangeById(4L);

        assertTrue(result.isPresent());
        assertEquals(4L, result.get().getId());
    }

    @Test
    void testGetExchangeById_NotFound() {
        when(exchangeRepository.findById(100L)).thenReturn(Optional.empty());
        Optional<ExchangeResponseDTO> result = exchangeService.getExchangeById(100L);
        assertFalse(result.isPresent());
    }

    @Test
    void testIsBookCopyInExchange_True() {
        when(exchangeRepository.isCurrentlyInExchange(10L)).thenReturn(true);
        assertTrue(exchangeService.isBookCopyInExchange(10L));
    }

    @Test
    void testIsBookCopyInExchange_False() {
        when(exchangeRepository.isCurrentlyInExchange(10L)).thenReturn(false);
        assertFalse(exchangeService.isBookCopyInExchange(10L));
    }

    @Test
    void testCreateExchange_Success() {
        ExchangeRequestDTO dto = new ExchangeRequestDTO();
        dto.setBookCopyId(10L);
        dto.setLocation("Test");

        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setId(10L);
        bookCopy.setOwnerId(2L);

        when(exchangeRepository.isCurrentlyInExchange(10L)).thenReturn(false);
        when(bookCopyRepository.findById(10L)).thenReturn(Optional.of(bookCopy));
        when(exchangeRepository.save(any(ExchangeEntity.class))).thenReturn(new ExchangeEntity());

        assertDoesNotThrow(() -> exchangeService.createExchange(dto, 1L));
        verify(exchangeRepository).save(any(ExchangeEntity.class));
    }

    @Test
    void testCreateExchange_AlreadyInExchange() {
        ExchangeRequestDTO dto = new ExchangeRequestDTO();
        dto.setBookCopyId(10L);

        when(exchangeRepository.isCurrentlyInExchange(10L)).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> exchangeService.createExchange(dto, 1L));
        verify(exchangeRepository, never()).save(any());
    }

    @Test
    void testRejectExchange_Success() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(1L);
        entity.setToUserId(2L);
        entity.setStatus(0);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(exchangeRepository.save(any(ExchangeEntity.class))).thenReturn(entity);

        assertDoesNotThrow(() -> exchangeService.rejectExchange(1L, 1L));
        assertEquals(-1, entity.getStatus());
        verify(exchangeRepository).save(entity);
    }

    @Test
    void testRejectExchange_NotFound() {
        when(exchangeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> exchangeService.rejectExchange(1L, 1L));
    }

    @Test
    void testRejectExchange_NotAllowed() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(1L);
        entity.setToUserId(2L);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(SecurityException.class, () -> exchangeService.rejectExchange(1L, 3L));
    }

    @Test
    void testSendExchange_Success() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(1L);
        entity.setStatus(0);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(exchangeRepository.save(any(ExchangeEntity.class))).thenReturn(entity);

        assertDoesNotThrow(() -> exchangeService.sendExchange(1L, 1L));
        assertEquals(1, entity.getStatus());
        verify(exchangeRepository).save(entity);
    }

    @Test
    void testSendExchange_WrongStatus() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(1L);
        entity.setStatus(2);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(IllegalArgumentException.class, () -> exchangeService.sendExchange(1L, 1L));
    }

    @Test
    void testSendExchange_NotAllowed() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(2L);
        entity.setStatus(0);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(SecurityException.class, () -> exchangeService.sendExchange(1L, 1L));
    }

    @Test
    void testAcceptExchange_Success() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(2L);
        entity.setToUserId(1L);
        entity.setStatus(1);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(exchangeRepository.save(any(ExchangeEntity.class))).thenReturn(entity);

        assertDoesNotThrow(() -> exchangeService.acceptExchange(1L, 1L));
        assertEquals(1, entity.getStatus());
        verify(exchangeRepository).save(entity);
    }

    @Test
    void testAcceptExchange_WrongStatus() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(2L);
        entity.setToUserId(1L);
        entity.setStatus(0);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(IllegalArgumentException.class, () -> exchangeService.acceptExchange(1L, 1L));
    }

    @Test
    void testAcceptExchange_NotAllowed() {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(1L);
        entity.setFromUserId(2L);
        entity.setToUserId(2L);
        entity.setStatus(1);

        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThrows(SecurityException.class, () -> exchangeService.acceptExchange(1L, 1L));
    }

    // Вспомогательный метод для моков toDto
    private void mockDtoDependencies(ExchangeEntity entity) {
        BookCopyEntity bookCopy = new BookCopyEntity();
        bookCopy.setId(entity.getBookCopyId());
        bookCopy.setOwnerId(entity.getToUserId());
        when(bookCopyRepository.findById(entity.getBookCopyId())).thenReturn(Optional.of(bookCopy));

        UserDTO fromUser = new UserDTO();
        fromUser.setId(entity.getFromUserId());
        UserDTO toUser = new UserDTO();
        toUser.setId(entity.getToUserId());
        when(userRepository.getUserById(entity.getFromUserId())).thenReturn(fromUser);
        when(userRepository.getUserById(entity.getToUserId())).thenReturn(toUser);
    }
}