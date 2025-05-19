package com.dmitry.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO сделки")
public class ExchangeResponseDTO {
    
    @Schema(description = "Идентификатор сделки")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Экземпляр книги")
    @JsonProperty("bookCopy")
    private BookCopyResponseDTO bookCopy;

    @Schema(description = "Отправитель книги")
    @JsonProperty("from")
    private UserDTO from;

    @Schema(description = "Получатель книги")
    @JsonProperty("to")
    private UserDTO to;

    @Schema(description = "Место обмена")
    @JsonProperty("location")
    private String location;

    @Schema(description = "Текущий статус сделки")
    @JsonProperty("status")
    private String status;

}
