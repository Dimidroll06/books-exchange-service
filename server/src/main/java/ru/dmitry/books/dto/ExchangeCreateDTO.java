package ru.dmitry.books.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExchangeCreateDTO {

    @NotNull
    private Long bookCopyId;

    @NotNull
    private String location;
}