package ru.dmitry.books.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.BookCopyDTO;
import ru.dmitry.books.dto.BookCopyResponseDTO;
import ru.dmitry.books.service.BookCopyService;

/**
 * 
 * @author dmitry
 */
@RestController
@RequestMapping("/api/v1/book-copy")
@RequiredArgsConstructor
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @Operation(summary = "Создать копию книги")
    @PostMapping
    public ResponseEntity<BookCopyResponseDTO> createBookCopy(
            @Valid @RequestBody BookCopyDTO bookCopy) {
        BookCopyResponseDTO createdCopy = bookCopyService.createBookCopy(bookCopy);
        return ResponseEntity.ok(createdCopy);
    }

    @Operation(summary = "Получить копии книги по ID книги")
    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<Page<BookCopyResponseDTO>> getCopiesByBookId(
            @Parameter(description = "ID книги", example = "1")
            @PathVariable Long bookId,

            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<BookCopyResponseDTO> copies = bookCopyService.getCopiesByBookId(bookId, pageable);
        return ResponseEntity.ok(copies);
    }

    @Operation(summary = "Получить копии книги по ID владельца")
    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<Page<BookCopyResponseDTO>> getCopiesByOwnerId(
            @Parameter(description = "ID владельца", example = "42")
            @PathVariable Long ownerId,

            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<BookCopyResponseDTO> copies = bookCopyService.getCopiesByOwnerId(ownerId, pageable);
        return ResponseEntity.ok(copies);
    }
}