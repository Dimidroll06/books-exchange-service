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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.BookCopyRequestDTO;
import com.dmitry.books.dto.BookCopyResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.service.BookCopyService;
import com.dmitry.books.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;



@RestController
@AllArgsConstructor
@RequestMapping("/copies")
public class BookCopyController {
    
    @Autowired
    private final BookCopyService bookCopyService;

    @Operation(summary = "Получить экземпляры книг по ID владельца")
    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<PageDTO<BookCopyResponseDTO>> getBooksByOwnerId(
            @Parameter(description = "Идентификатор владельца") @PathVariable(required = true) Long ownerId,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<BookCopyResponseDTO> bookCopies = bookCopyService.getByOwnerId(ownerId, pageable);
        return ResponseEntity.ok(bookCopies);
    }

    @Operation(summary = "Получить экземпляры книг по ID книги")
    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<PageDTO<BookCopyResponseDTO>> getBooksByBookId(
            @Parameter(description = "Идентификатор книги") @PathVariable(required = true) Long bookId,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<BookCopyResponseDTO> bookCopies = bookCopyService.getByBookId(bookId, pageable);
        return ResponseEntity.ok(bookCopies);
    }

    @Operation(summary = "Получить экземпляр книги по ID")
    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponseDTO> getBookById(
            @Parameter(description = "ID экземпляра книги") @PathVariable Long id) {
        
        Optional<BookCopyResponseDTO> bookCopy = bookCopyService.getBookCopyById(id);
        return  bookCopy.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Создать экземпляр книги")
    @PostMapping
    public ResponseEntity<Void> createBookCopy(
        @Valid @Parameter(description = "Даныне для создания экземпляра книги") @RequestBody BookCopyRequestDTO bookCopyRequestDTO) {
        
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userData.getUserId();

        bookCopyService.createBookCopy(userId, bookCopyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    // обновление данных книги будет реализовано в системе сделок

    @Operation(summary = "Удалить экземпляр книги по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookCopy(
        @Parameter(description = "ID экземпляра книги") @PathVariable Long id) {
        
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        bookCopyService.deleteBookCopy(id, userData);
        return ResponseEntity.noContent().build();
    }
    
}
