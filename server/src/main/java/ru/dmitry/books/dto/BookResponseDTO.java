package ru.dmitry.books.dto;

/**
 *
 * @author dmitry
 */
public record BookResponseDTO(
        Long id,
        String title,
        String author,
        String description,
        String genre) {
}