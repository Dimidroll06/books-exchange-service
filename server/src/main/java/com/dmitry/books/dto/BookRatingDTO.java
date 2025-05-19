package com.dmitry.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для рейтинга книги")
public class BookRatingDTO {

    @Schema(description = "Идентификатор книги")
    @JsonProperty("bookId")
    private Long bookId;
    
    @Schema(description = "Количество отзывов")
    @JsonProperty("reviewCount")
    private Long reviewCount;

    @Schema(description = "Средний рейтинг")
    @JsonProperty("averageRating")
    private Double averageRating;
}
