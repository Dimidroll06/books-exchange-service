package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO возвращаемого экземпляра книги")
public class BookResponseDTO {

    @Schema(description = "Идентификатор книги", example = "1")
    private Long id;

    @Schema(description = "Название книги", example = "Шестёрка воронов")
    private String title;

    @Schema(description = "Автор книги", example = "Ли Бардуго")
    private String author;

    @Schema(description = "Описание книги", example = "Шесть героев, одно опасное дело")
    private String description;

    @Schema(description = "Жанр книги", example = "Фантастика")
    private String genre;

}
