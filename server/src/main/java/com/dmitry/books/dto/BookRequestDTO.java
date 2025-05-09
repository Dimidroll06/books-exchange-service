package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO для создания экземпляра книги")
public class BookRequestDTO {

    @NotBlank(message = "Название книги не должно быть пустым")
    @Schema(description = "Название книги", example = "Шестёрка воронов")
    private String title;

    @NotBlank(message = "Автор книги не должен быть пустым")
    @Schema(description = "Автор книги", example = "Ли Бардуго")
    private String author;

    @NotBlank(message = "Описание книги не должно быть пустым")
    @Schema(description = "Описание книги", example = "Шесть героев, одно опасное дело")
    private String description;

    @NotNull(message = "ID жанра книги не должен быть null")
    @Schema(description = "ID жанра книги", example = "1")
    private Long genreId;

}