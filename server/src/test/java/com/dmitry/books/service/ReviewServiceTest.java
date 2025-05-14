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
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dmitry.books.dto.BookRatingDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.dto.ReviewRequestDTO;
import com.dmitry.books.dto.ReviewResponseDTO;
import com.dmitry.books.dto.UserDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.model.ReviewEntity;
import com.dmitry.books.repository.BookRepository;
import com.dmitry.books.repository.ReviewRepository;
import com.dmitry.books.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewEntity reviewEntity;
    private BookEntity bookEntity;
    private UserDTO userDTO;
    private ReviewRequestDTO reviewRequestDTO;

    @BeforeEach
    void setUp() {
        reviewEntity = new ReviewEntity();
        reviewEntity.setId(1L);
        reviewEntity.setBookId(1L);
        reviewEntity.setUserId(1L);
        reviewEntity.setRating(5);
        reviewEntity.setComment("Great book!");

        bookEntity = new BookEntity();
        bookEntity.setId(1L);
        bookEntity.setTitle("Test Book");
        bookEntity.setAuthor("Test Author");
        bookEntity.setDescription("Test Description");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");

        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setBookId(1L);
        reviewRequestDTO.setRating(5);
        reviewRequestDTO.setComment("Great book!");
    }

    @Test
    void testGetReviewsByFilter() {
        Page<ReviewEntity> page = new PageImpl<>(Collections.singletonList(reviewEntity));
        when(reviewRepository.findByFilters(anyLong(), anyLong(), any(Pageable.class))).thenReturn(page);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));
        when(userRepository.getUserById(anyLong())).thenReturn(userDTO);

        Pageable pageable = PageRequest.of(0, 10);
        PageDTO<ReviewResponseDTO> result = reviewService.getReviewsByFilter(1L, 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Great book!", result.getContent().get(0).getComment());
        assertEquals("testuser", result.getContent().get(0).getUser().getUsername());
    }

    @Test
    void testGetReviewById() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewEntity));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));
        when(userRepository.getUserById(anyLong())).thenReturn(userDTO);

        Optional<ReviewResponseDTO> result = reviewService.getReviewById(1L);

        assertTrue(result.isPresent());
        assertEquals("Great book!", result.get().getComment());
        assertEquals("Test Book", result.get().getBook().getTitle());
    }

    @Test
    void testGetReviewById_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ReviewResponseDTO> result = reviewService.getReviewById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetBookRatingById() {
        BookRatingDTO ratingDTO = new BookRatingDTO();
        ratingDTO.setAverageRating(4.5);
        ratingDTO.setReviewCount(10L);

        when(reviewRepository.findBookRatingInfo(1L)).thenReturn(ratingDTO);

        BookRatingDTO result = reviewService.getBookRatingById(1L);

        assertNotNull(result);
        assertEquals(4.5, result.getAverageRating());
        assertEquals(10L, result.getReviewCount());
    }

    @Test
    void testCreateReview_Success() {
        when(reviewRepository.findByFilters(anyLong(), anyLong(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(reviewEntity);

        assertDoesNotThrow(() -> reviewService.createReview(1L, reviewRequestDTO));
        verify(reviewRepository, times(1)).save(any(ReviewEntity.class));
    }

    @Test
    void testCreateReview_AlreadyExists() {
        Page<ReviewEntity> page = new PageImpl<>(Collections.singletonList(reviewEntity));
        when(reviewRepository.findByFilters(anyLong(), anyLong(), any())).thenReturn(page);

        assertThrows(EntityExistsException.class, 
            () -> reviewService.createReview(1L, reviewRequestDTO));
    }

    @Test
    void testUpdateReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewEntity));
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(reviewEntity);

        ReviewRequestDTO updatedReview = new ReviewRequestDTO();
        updatedReview.setBookId(1L);
        updatedReview.setRating(4);
        updatedReview.setComment("Good book");

        assertDoesNotThrow(() -> reviewService.updateReview(1L, 1L, updatedReview));
        verify(reviewRepository, times(1)).save(reviewEntity);
    }

    @Test
    void testUpdateReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> reviewService.updateReview(1L, 1L, reviewRequestDTO));
    }

    @Test
    void testUpdateReview_AccessDenied() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewEntity));

        assertThrows(SecurityException.class,
            () -> reviewService.updateReview(1L, 2L, reviewRequestDTO));
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewEntity));
        doNothing().when(reviewRepository).deleteById(1L);

        assertDoesNotThrow(() -> reviewService.deleteReview(1L, 1L));
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
            () -> reviewService.deleteReview(1L, 1L));
    }

    @Test
    void testDeleteReview_AccessDenied() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewEntity));

        assertThrows(SecurityException.class,
            () -> reviewService.deleteReview(1L, 2L));
    }
}