package ru.dmitry.books.dto;

/**
 *
 * @author dmitry
 */
public record BookCreateDTO(
        String title,
        String author,
        String description,
        Long genreId) {
}