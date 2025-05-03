package ru.dmitry.books.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenreUpdateDTO {
    @NotNull
    private String name;
}