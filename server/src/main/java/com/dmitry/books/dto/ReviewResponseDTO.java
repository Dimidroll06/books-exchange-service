package com.dmitry.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO возвращаемого отзыва")
public class ReviewResponseDTO {
    
    @Schema(description = "Идентификатор отзыва", example="1")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Пользователь, оставивший отзыв")
    @JsonProperty("user")
    private UserDTO user;

    @Schema(description = "Книга, на которую оставлен отзыв")
    @JsonProperty("book")
    private BookResponseDTO book;

    @Schema(description = "Оценка", example="10", minimum="1", maximum="10")
    @JsonProperty("rating")
    private Integer rating;

    @Schema(description = "Комментарий", example="Восхитительная книга")
    @JsonProperty("comment")
    private String comment;
}
