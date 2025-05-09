package com.dmitry.books.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmitry.books.dto.BookRequestDTO;
import com.dmitry.books.dto.BookResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "Получить список книг с фильтрацией и пагинацией")
    @GetMapping
    public ResponseEntity<PageDTO<BookResponseDTO>> getBooks(
            @Parameter(description = "Автор книги") @RequestParam(required = false) String author,
            @Parameter(description = "ID жанра") @RequestParam(required = false) Long genreId,
            @Parameter(description = "Название книги") @RequestParam(required = false) String title,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageDTO<BookResponseDTO> books = bookService.getBooksByFilter(author, genreId, title, pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Получить книгу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(
            @Parameter(description = "ID книги") @PathVariable Long id) {

        Optional<BookResponseDTO> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Создать новую книгу")
    @PostMapping
    public ResponseEntity<Void> createBook(
            @Parameter(description = "Данные для создания книги") @RequestBody BookRequestDTO bookRequestDTO) {

        bookService.createBook(bookRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Обновить данные книги")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(
            @Parameter(description = "ID книги") @PathVariable Long id,
            @Parameter(description = "Обновленные данные книги") @RequestBody BookRequestDTO bookRequestDTO) {

        bookService.updateBook(id, bookRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить книгу по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID книги") @PathVariable Long id) {

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
