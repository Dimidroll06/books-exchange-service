package com.dmitry.books.controller;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.ExchangeRequestDTO;
import com.dmitry.books.dto.ExchangeResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.exception.GlobalExceptionHandler;
import com.dmitry.books.service.ExchangeService;
import com.dmitry.books.util.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class ExchangeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExchangeService exchangeService;

    @InjectMocks
    private ExchangeController exchangeController;

    private AuthFilter.UserData userData;
    private ExchangeResponseDTO exchangeResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userData = new AuthFilter.UserData();
        userData.setUserId(1L);
        userData.setUsername("testuser");
        userData.setAdmin(false);

        exchangeResponseDTO = new ExchangeResponseDTO();
        exchangeResponseDTO.setId(1L);
    }

    @Test
    void testGetExchangesBySenderId() throws Exception {
        PageDTO<ExchangeResponseDTO> pageDTO = new PageDTO<>();
        when(exchangeService.getExchangeBySenderId(eq(1L), any(Pageable.class))).thenReturn(pageDTO);

        mockMvc.perform(get("/exchange/by-sender/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(exchangeService).getExchangeBySenderId(1L, PageRequest.of(0, 10));
    }

    @Test
    void testGetExchangesByGetterId() throws Exception {
        PageDTO<ExchangeResponseDTO> pageDTO = new PageDTO<>();
        when(exchangeService.getExchangeByGetterId(eq(2L), any(Pageable.class))).thenReturn(pageDTO);

        mockMvc.perform(get("/exchange/by-getter/2")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(exchangeService).getExchangeByGetterId(2L, PageRequest.of(0, 10));
    }

    @Test
    void testGetExchangesByUserId() throws Exception {
        PageDTO<ExchangeResponseDTO> pageDTO = new PageDTO<>();
        when(exchangeService.getExchangeByUserId(eq(3L), any(Pageable.class))).thenReturn(pageDTO);

        mockMvc.perform(get("/exchange/by-user/3")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(exchangeService).getExchangeByUserId(3L, PageRequest.of(0, 10));
    }

    @Test
    void testGetMyExchanges_Authorized() throws Exception {
        PageDTO<ExchangeResponseDTO> pageDTO = new PageDTO<>();
        when(exchangeService.getExchangeByUserId(eq(1L), any(Pageable.class))).thenReturn(pageDTO);

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(get("/exchange/my")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(exchangeService).getExchangeByUserId(1L, PageRequest.of(0, 10));
        }
    }

    @Test
    void testGetMyExchanges_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(get("/exchange/my")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testGetExchangeById_Found() throws Exception {
        when(exchangeService.getExchangeById(1L)).thenReturn(Optional.of(exchangeResponseDTO));

        mockMvc.perform(get("/exchange/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetExchangeById_NotFound() throws Exception {
        when(exchangeService.getExchangeById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/exchange/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateNewExchange_Authorized() throws Exception {
        ExchangeRequestDTO requestDTO = new ExchangeRequestDTO();
        requestDTO.setBookCopyId(10L);

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(post("/exchange")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "bookCopyId": 10,
                                        "location": "Test"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            verify(exchangeService).createExchange(any(ExchangeRequestDTO.class), eq(1L));
        }
    }

    @Test
    void testCreateNewExchange_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(post("/exchange")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "bookCopyId": 10,
                                        "location": "Test"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testRejectExchange_Authorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(put("/exchange/reject/1"))
                    .andExpect(status().isOk());

            verify(exchangeService).rejectExchange(1L, 1L);
        }
    }

    @Test
    void testRejectExchange_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(put("/exchange/reject/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testSendExchange_Authorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(put("/exchange/send/1"))
                    .andExpect(status().isOk());

            verify(exchangeService).sendExchange(1L, 1L);
        }
    }

    @Test
    void testSendExchange_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(put("/exchange/send/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testAcceptExchange_Authorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(put("/exchange/accept/1"))
                    .andExpect(status().isOk());

            verify(exchangeService).acceptExchange(1L, 1L);
        }
    }

    @Test
    void testAcceptExchange_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(put("/exchange/accept/1"))
                    .andExpect(status().isUnauthorized());
        }
    }
}