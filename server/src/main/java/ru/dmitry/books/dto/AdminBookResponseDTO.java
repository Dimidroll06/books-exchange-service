package ru.dmitry.books.dto;

import lombok.Data;

@Data
public class AdminBookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String genre;
}