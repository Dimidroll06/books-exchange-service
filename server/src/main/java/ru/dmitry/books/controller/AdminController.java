package ru.dmitry.books.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.AdminBookResponseDTO;
import ru.dmitry.books.dto.ExchangeStatsDTO;
import ru.dmitry.books.dto.GenreCreateDTO;
import ru.dmitry.books.dto.GenreUpdateDTO;
import ru.dmitry.books.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Получить все загруженные книги")
    @GetMapping("/books")
    public ResponseEntity<List<AdminBookResponseDTO>> getAllBooks(HttpServletRequest request) {
        checkAdminAccess(request);
        List<AdminBookResponseDTO> books = adminService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Получить данные по результатам сделок")
    @GetMapping("/exchanges/stats")
    public ResponseEntity<ExchangeStatsDTO> getExchangeStats(HttpServletRequest request) {
        checkAdminAccess(request);
        ExchangeStatsDTO stats = adminService.getExchangeStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Создать новый жанр")
    @PostMapping("/genres")
    public ResponseEntity<Void> createGenre(
            @Valid @RequestBody GenreCreateDTO genreCreateDTO,
            HttpServletRequest request) {
        checkAdminAccess(request);
        adminService.createGenre(genreCreateDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить жанр")
    @PutMapping("/genres/{id}")
    public ResponseEntity<Void> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreUpdateDTO genreUpdateDTO,
            HttpServletRequest request) {
        checkAdminAccess(request);
        adminService.updateGenre(id, genreUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить жанр")
    @DeleteMapping("/genres/{id}")
    public ResponseEntity<Void> deleteGenre(
            @PathVariable Long id,
            HttpServletRequest request) {
        checkAdminAccess(request);
        adminService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    private void checkAdminAccess(HttpServletRequest request) {
        Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            throw new SecurityException("Access denied: Admin privileges required");
        }
    }
}