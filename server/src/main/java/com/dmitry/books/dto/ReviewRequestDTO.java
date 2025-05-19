package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO для изменения отзыва")
public class ReviewRequestDTO {
    
    @NotNull(message = "ID книги не может быть null")
    @Schema(description = "Идентификатор книги", example = "1")
    private Long bookId;

    @NotNull(message = "Оценка должна присутствовать")
    @Max(10)
    @Min(1)
    @Schema(description = "Рейтинг от 0 до 10")
    private Integer rating;

    @Schema(description = "Комментарий к отзыву", example = "Прекрасное чтиво!")
    private String comment;
}
