package com.dmitry.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO возвращаемого экземпляра книги")
public class BookCopyResponseDTO {
    
    @Schema(description = "Идентификатор экземпляра книги")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Идентификатор книги")
    @JsonProperty("bookId")
    private Long bookId;

    @Schema(description = "Идентификатор текущего владельца экземпляра")
    @JsonProperty("ownerId")
    private Long ownerId;
}
