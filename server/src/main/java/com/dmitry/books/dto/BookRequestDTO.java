package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для создания экземпляра книги")
public class BookRequestDTO {

    @Schema(description = "Название книги", example = "Шестёрка воронов")
    private String title;

    @Schema(description = "Автор книги", example = "Ли Бардуго")
    private String author;

    @Schema(description = "Описание книги", example = "Шесть героев, одно опасное дело")
    private String description;

    @Schema(description = "ID жанра книги", example = "1")
    private Long genreId;

}