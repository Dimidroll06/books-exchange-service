package com.dmitry.books.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.ExchangeStatusStatDTO;
import com.dmitry.books.dto.GenreCreateDTO;
import com.dmitry.books.exception.GlobalExceptionHandler;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.service.AdminService;
import com.dmitry.books.util.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private AuthFilter.UserData adminUser;
    private AuthFilter.UserData regularUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
                
        adminUser = new AuthFilter.UserData();
        adminUser.setUserId(1L);
        adminUser.setUsername("admin");
        adminUser.setAdmin(true);

        regularUser = new AuthFilter.UserData();
        regularUser.setUserId(2L);
        regularUser.setUsername("user");
        regularUser.setAdmin(false);
    }

    @Test
    void getAllBooks_AdminAuthorized() throws Exception {
        when(adminService.getAllBooks()).thenReturn(List.of(new BookEntity()));

        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(adminUser);

            mockMvc.perform(get("/admin/books"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getAllBooks_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(get("/admin/books"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void getAllBooks_NotAdmin() throws Exception {
        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(regularUser);

            mockMvc.perform(get("/admin/books"))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void getExchangeStats_AdminAuthorized() throws Exception {
        when(adminService.getExchangeStats()).thenReturn(List.of(new ExchangeStatusStatDTO(1, 10)));

        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(adminUser);

            mockMvc.perform(get("/admin/exchange-stats"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getExchangeStats_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(get("/admin/exchange-stats"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void getExchangeStats_NotAdmin() throws Exception {
        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(regularUser);

            mockMvc.perform(get("/admin/exchange-stats"))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void addGenre_AdminAuthorized() throws Exception {
        GenreCreateDTO dto = new GenreCreateDTO();
        dto.setName("TestGenre");

        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(adminUser);

            mockMvc.perform(post("/admin/genres")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"TestGenre\"}"))
                    .andExpect(status().isCreated());

            verify(adminService).addGenre(any(GenreCreateDTO.class));
        }
    }

    @Test
    void addGenre_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(post("/admin/genres")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"TestGenre\"}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void addGenre_NotAdmin() throws Exception {
        try (MockedStatic<SecurityUtils> utils = mockStatic(SecurityUtils.class)) {
            utils.when(SecurityUtils::getCurrentUser).thenReturn(regularUser);

            mockMvc.perform(post("/admin/genres")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"TestGenre\"}"))
                    .andExpect(status().isForbidden());
        }
    }
}