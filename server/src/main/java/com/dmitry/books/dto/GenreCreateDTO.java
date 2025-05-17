package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для добавления жанра в сервис")
public class GenreCreateDTO {
    @Schema(description = "Название жанра")
    private String name;
}