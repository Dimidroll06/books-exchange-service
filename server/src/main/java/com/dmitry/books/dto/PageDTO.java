package com.dmitry.books.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO Ответа в виде страниц")
public class PageDTO<T> {
    @Schema(description = "Список элементов на странице")
    @JsonProperty("content")
    private List<T> content = List.of();

    @Schema(description = "Номер текущей страницы (начиная с 0)")
    @JsonProperty("pageNumber")
    private int pageNumber;

    @Schema(description = "Размер страницы")
    @JsonProperty("pageSize")
    private int pageSize;

    @Schema(description = "Общее количество элементов")
    @JsonProperty("totalElements")
    private long totalElements;

    @Schema(description = "Общее количество страниц")
    @JsonProperty("totalPages")
    private int totalPages;

    @Schema(description = "Флаг, указывающий, является ли текущая страница последней")
    @JsonProperty("isLast")
    private boolean last;
}
