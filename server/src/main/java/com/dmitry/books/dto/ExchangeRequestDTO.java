package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO для создания сделки")
public class ExchangeRequestDTO {
    
    @NotNull(message = "ID экземпляра книги не может быть null")
    @Schema(description = "ID экземпляра книги")
    private Long bookCopyId;

    @NotBlank(message = "Вы должны указать адресс")
    @Schema(description = "Местоположение, где произойдёт обмен")
    private String location;
}
