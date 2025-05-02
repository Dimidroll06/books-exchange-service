package ru.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для создания или обновления отзыва")
public record ReviewDTO(
        @NotNull
        @Schema(description = "ID книги", example = "1")
        Long bookId,

        /* 
         * TODO: 
         * - убрать
         * - сделать в сервисе автоматическое добавление из получаемого JWT
        */
        Long userId,

        @NotNull
        @Min(0)
        @Max(5)
        @Schema(description = "Рейтинг (0-5)", example = "4")
        Integer rating,

        @Schema(description = "Комментарий", example = "Отличная книга!")
        String comment) {
}