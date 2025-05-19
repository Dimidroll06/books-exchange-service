package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для создания экземпляра книги")
public class BookCopyRequestDTO {
    @NotBlank(message = "ID книги не может быть null")
    @Schema(description = "ID книги", example = "1")
    private Long bookId;
}
