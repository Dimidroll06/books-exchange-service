package ru.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author dmitry
 */
@Schema(description = "DTO для создания книги")
public record BookCreateDTO(
        @NotNull
        @Size(min = 1, max = 255)
        @Schema(description = "Название книги", example = "Евгений Онегин")
        String title,

        @NotNull
        @Size(min = 1, max = 255)
        @Schema(description = "Автор книги", example = "А.С. Пушкин")
        String author,

        @Schema(description = "Описание книги", example = "Роман о сигма бое")
        String description,

        @NotNull
        @Schema(description = "ID жанра книги", example = "1")
        Long genreId) {
}