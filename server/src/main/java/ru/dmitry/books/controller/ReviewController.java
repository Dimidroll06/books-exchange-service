package ru.dmitry.books.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.ReviewDTO;
import ru.dmitry.books.dto.ReviewResponseDTO;
import ru.dmitry.books.service.ReviewService;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Создать отзыв")
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewDTO review) {
        ReviewResponseDTO createdReview = reviewService.createReview(review);
        return ResponseEntity.ok(createdReview);
    }

    @Operation(summary = "Получить отзывы по ID книги")
    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByBookId(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long bookId,

            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<ReviewResponseDTO> reviews = reviewService.getReviewsByBookId(bookId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Получить отзывы по ID пользователя")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByUserId(
            @Parameter(description = "ID пользователя", example = "42")
            @PathVariable Long userId,

            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<ReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Обновить отзыв")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @Parameter(description = "ID отзыва", example = "1")
            @PathVariable Long id,

            @Valid @RequestBody ReviewDTO review) {
        ReviewResponseDTO updatedReview = reviewService.updateReview(id, review);
        return ResponseEntity.ok(updatedReview);
    }

    @Operation(summary = "Удалить отзыв")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID отзыва", example = "1")
            @PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}