package com.dmitry.books.controller;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doNothing;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.dto.ReviewRequestDTO;
import com.dmitry.books.dto.ReviewResponseDTO;
import com.dmitry.books.exception.GlobalExceptionHandler;
import com.dmitry.books.service.ReviewService;
import com.dmitry.books.util.SecurityUtils;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewResponseDTO reviewResponseDTO;
    private ReviewRequestDTO reviewRequestDTO;
    private AuthFilter.UserData userData;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(1L);
        reviewResponseDTO.setRating(5);
        reviewResponseDTO.setComment("Great book!");

        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setBookId(1L);
        reviewRequestDTO.setRating(5);
        reviewRequestDTO.setComment("Great book!");
        
        userData = new AuthFilter.UserData();
        userData.setUserId(1L);
        userData.setUsername("testuser");
        userData.setAdmin(false);
    }

    @Test
    void testGetReviewsByBookId() throws Exception {
        PageDTO<ReviewResponseDTO> pageDTO = new PageDTO<>();
        when(reviewService.getReviewsByFilter(isNull(), eq(1L), any(Pageable.class))).thenReturn(pageDTO);

        mockMvc.perform(get("/rewiew/by-book/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        verify(reviewService).getReviewsByFilter(null, 1L, PageRequest.of(0, 10));
    }

    @Test
    void testGetReviewsByUserId() throws Exception {
        PageDTO<ReviewResponseDTO> pageDTO = new PageDTO<>();
        when(reviewService.getReviewsByFilter(eq(1L), isNull(), any(Pageable.class))).thenReturn(pageDTO);

        mockMvc.perform(get("/rewiew/by-user/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        verify(reviewService).getReviewsByFilter(1L, null, PageRequest.of(0, 10));
    }

    @Test
    void testGetReviewById_Found() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(Optional.of(reviewResponseDTO));

        mockMvc.perform(get("/rewiew/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great book!"));
    }

    @Test
    void testGetReviewById_NotFound() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rewiew/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void testCreateNewReview_Success() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);
            
            doNothing().when(reviewService).createReview(anyLong(), any(ReviewRequestDTO.class));

            mockMvc.perform(post("/rewiew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "bookId": 1,
                                "rating": 5,
                                "comment": "Great book!"
                            }
                            """))
                    .andExpect(status().isCreated());

            verify(reviewService).createReview(eq(1L), any(ReviewRequestDTO.class));
        }
    }

    @Test
    void testCreateNewReview_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(post("/rewiew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "bookId": 1,
                                "rating": 5,
                                "comment": "Great book!"
                            }
                            """))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testUpdateReview_Success() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(put("/rewiew/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "bookId": 1,
                                "rating": 4,
                                "comment": "Good book"
                            }
                            """))
                    .andExpect(status().isOk());

            verify(reviewService).updateReview(eq(1L), eq(1L), any(ReviewRequestDTO.class));
        }
    }

    @Test
    void testUpdateReview_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(put("/rewiew/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "bookId": 1,
                                "rating": 4,
                                "comment": "Good book"
                            }
                            """))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void testDeleteReview_Success() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(userData);

            mockMvc.perform(delete("/rewiew/1"))
                    .andExpect(status().isNoContent());

            verify(reviewService).deleteReview(1L, 1L);
        }
    }

    @Test
    void testDeleteReview_Unauthorized() throws Exception {
        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUser).thenReturn(null);

            mockMvc.perform(delete("/rewiew/1"))
                    .andExpect(status().isUnauthorized());
        }
    }
}