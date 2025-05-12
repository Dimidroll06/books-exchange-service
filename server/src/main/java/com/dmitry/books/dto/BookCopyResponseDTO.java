package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO возвращаемого экземпляра книги")
public class BookCopyResponseDTO {
    
    @Schema(description = "Идентификатор экземпляра книги")
    private Long id;

    @Schema(description = "Идентификатор книги")
    private Long bookId;

    @Schema(description = "Идентификатор текущего владельца экземпляра")
    private Long ownerId;
}
