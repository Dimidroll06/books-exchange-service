package ru.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для ответа с информацией об отзыве")
public record ReviewResponseDTO(
        Long id,
        Long bookId,
        String bookTitle,
        Long userId,
        Integer rating,
        String comment) {
}