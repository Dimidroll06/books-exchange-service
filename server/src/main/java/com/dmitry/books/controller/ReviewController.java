package com.dmitry.books.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.dto.ReviewRequestDTO;
import com.dmitry.books.dto.ReviewResponseDTO;
import com.dmitry.books.service.ReviewService;
import com.dmitry.books.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;



@RestController
@AllArgsConstructor
@RequestMapping("/rewiew")
public class ReviewController {
    
    @Autowired
    private final ReviewService reviewService;

    @Operation(summary = "Получить отзывы по id книги")
    @GetMapping("/by-book/{id}")
    public ResponseEntity<PageDTO<ReviewResponseDTO>> getReviewsByBookId(
            @Parameter(description = "ID Книги") @PathVariable Long id,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ReviewResponseDTO> reviews = reviewService.getReviewsByFilter(null, id, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Получить отзывы пользователя")
    @GetMapping("/by-user/{id}")
    public ResponseEntity<PageDTO<ReviewResponseDTO>> getReviewsByUserId(
            @Parameter(description = "ID Пользователя") @PathVariable Long id,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ReviewResponseDTO> reviews = reviewService.getReviewsByFilter(id, null, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Получить отзыв по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getMethodName(
            @Parameter(description = "ID отзыва") @PathVariable Long id) {
        Optional<ReviewResponseDTO> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @Operation(summary = "Написать отзыв")
    @PostMapping
    public ResponseEntity<Void> createNewReview(
            @Parameter(description = "Отзыв") @PathVariable ReviewRequestDTO review) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reviewService.createReview(userData.getUserId(), review);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @Operation(summary = "Обновить отзыв")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReview(
            @Parameter(description = "ID отзыва") @PathVariable Long id,
            @Parameter(description = "Изменённый отзыв") @RequestBody ReviewRequestDTO updatedReview) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reviewService.updateReview(id, userData.getUserId(), updatedReview);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить отзыв")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID отзыва") @PathVariable Long id) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reviewService.deleteReview(id, userData.getUserId());
        return ResponseEntity.noContent().build();
    }
}
