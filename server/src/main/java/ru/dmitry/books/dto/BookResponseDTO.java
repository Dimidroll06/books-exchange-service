package ru.dmitry.books.dto;

/**
 *
 * @author dmitry
 */
public record BookResponseDTO(
    String title,
    String author,
    String description,
    String genre
) {}