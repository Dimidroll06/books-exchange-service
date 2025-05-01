package ru.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 
 * @author dmitry
 */
@Schema(description = "DTO для создания копии книги")
public record BookCopyDTO(
        @NotNull
        @Schema(description = "ID книги", example = "1")
        Long bookId,

        @NotNull
        @Schema(description = "ID владельца копии", example = "42")
        Long ownerId) {
}