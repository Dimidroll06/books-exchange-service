package com.dmitry.books.controller;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.BookCopyRequestDTO;
import com.dmitry.books.dto.BookCopyResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.exception.GlobalExceptionHandler;
import com.dmitry.books.service.BookCopyService;
import com.dmitry.books.util.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class BookCopyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookCopyService bookCopyService;

    @InjectMocks
    private BookCopyController bookCopyController;

    private BookCopyResponseDTO bookCopyResponseDTO;
    private BookCopyRequestDTO bookCopyRequestDTO;
    private AuthFilter.UserData userData;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookCopyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        bookCopyResponseDTO = new BookCopyResponseDTO();
        bookCopyResponseDTO.setId(1L);
        bookCopyResponseDTO.setBookId(1L);
        bookCopyResponseDTO.setOwnerId(1L);

        bookCopyRequestDTO = new BookCopyRequestDTO();
        bookCopyRequestDTO.setBookId(1L);

        userData = new AuthFilter.UserData();
        userData.setUserId(1L);
        userData.setUsername("testuser");
        userData.setAdmin(false);
    }

    @Test
    void testGetBooksByOwnerId() throws Exception {
        PageDTO<BookCopyResponseDTO> pageDTO = new PageDTO<>();
        when(bookCopyService.getByOwnerId(anyLong(), any(Pageable.class))).thenReturn(pageDTO);

        mockMvc.perform(get("/copies/by-owner/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(bookCopyService).getByOwnerId(1L, PageRequest.of(0, 10));
    }

    @Test
    void testGetBookById_Found() throws Exception {
        when(bookCopyService.getBookCopyById(1L)).thenReturn(Optional.of(bookCopyResponseDTO));

        mockMvc.perform(get("/copies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.ownerId").value(1));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookCopyService.getBookCopyById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/copies/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBookCopy_Success() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(post("/copies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "bookId": 1,
                                "ownerId": 1
                            }
                            """))
                    .andExpect(status().isCreated());

            verify(bookCopyService).createBookCopy(1L, bookCopyRequestDTO);
        }
    }

    @Test
    void testCreateBookCopy_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(post("/copies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "bookId": 1,
                                "ownerId": 1
                            }
                            """))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testDeleteBookCopy_Success() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(delete("/copies/1"))
                    .andExpect(status().isNoContent());

            verify(bookCopyService).deleteBookCopy(1L, userData);
        }
    }

    @Test
    void testDeleteBookCopy_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(delete("/copies/1"))
                    .andExpect(status().isUnauthorized());
        }
    }
}