package com.dmitry.books.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO Ответа в виде страниц")
public class PageDTO<T> {
    @Schema(description = "Список элементов на странице")
    private List<T> content = List.of();

    @Schema(description = "Номер текущей страницы (начиная с 0)")
    private int pageNumber;

    @Schema(description = "Размер страницы")
    private int pageSize;

    @Schema(description = "Общее количество элементов")
    private long totalElements;

    @Schema(description = "Общее количество страниц")
    private int totalPages;

    @Schema(description = "Флаг, указывающий, является ли текущая страница последней")
    private boolean last;
}
