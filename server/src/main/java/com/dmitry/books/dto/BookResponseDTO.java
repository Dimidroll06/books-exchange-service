package com.dmitry.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO возвращаемой книги")
public class BookResponseDTO {

    @Schema(description = "Идентификатор книги", example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Название книги", example = "Шестёрка воронов")
    @JsonProperty("title")
    private String title;

    @Schema(description = "Автор книги", example = "Ли Бардуго")
    @JsonProperty("author")
    private String author;

    @Schema(description = "Описание книги", example = "Шесть героев, одно опасное дело")
    @JsonProperty("description")
    private String description;

    @Schema(description = "Жанр книги", example = "Фантастика")
    @JsonProperty("genre")
    private String genre;

}
