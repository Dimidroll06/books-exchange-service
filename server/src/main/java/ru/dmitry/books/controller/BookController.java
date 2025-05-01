package ru.dmitry.books.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.BookCreateDTO;
import ru.dmitry.books.dto.BookResponseDTO;
import ru.dmitry.books.service.BookService;

/**
 *
 * @author dmitry
 */
@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Создать новую книгу")
    @PostMapping
    public ResponseEntity<BookResponseDTO> postBook(
            @Valid @RequestBody BookCreateDTO book) {
        BookResponseDTO createdBook = bookService.createBook(book);
        return ResponseEntity.ok(createdBook);
    }

    @Operation(summary = "Получить список книг с фильтрацией")
    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(
            @Parameter(description = "Название книги для фильтрации", example = "Война и мир")
            @RequestParam(required = false) String title,

            @Parameter(description = "Автор книги для фильтрации", example = "Лев Толстой")
            @RequestParam(required = false) String author,

            @Parameter(description = "ID жанра для фильтрации", example = "1")
            @RequestParam(required = false) Long genreId,

            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<BookResponseDTO> books = bookService.findBooksByFilters(title, author, genreId, pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Получить книгу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Обновить книгу по ID")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> putBook(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long id,

            @Valid @RequestBody BookCreateDTO book) {
        BookResponseDTO updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "Удалить книгу по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
