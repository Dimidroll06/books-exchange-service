package ru.dmitry.books.dto;

/**
 * DTO для ответа с информацией о копии книги.
 */
public record BookCopyResponseDTO(
        Long id,
        Long bookId,
        String bookTitle,
        Long ownerId) {
}