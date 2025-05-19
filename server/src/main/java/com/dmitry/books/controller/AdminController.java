package com.dmitry.books.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.ExchangeStatusStatDTO;
import com.dmitry.books.dto.GenreCreateDTO;
import com.dmitry.books.model.BookEntity;
import com.dmitry.books.service.AdminService;
import com.dmitry.books.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private final AdminService adminService;

    @Operation(description = "Доступно только администраторам")
    @GetMapping("/books")
    public ResponseEntity<List<BookEntity>> getAllBooks() {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!userData.isAdmin()) {
            throw new SecurityException("Доступ только для администраторов");
        }

        return ResponseEntity.ok(adminService.getAllBooks());
    }

    @Operation(summary = "Получить статистику обменов")
    @GetMapping("/exchange-stats")
    public ResponseEntity<List<ExchangeStatusStatDTO>> getExchangeStats() {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!userData.isAdmin()) {
            throw new SecurityException("Доступ только для администраторов");
        }

        return ResponseEntity.ok(adminService.getExchangeStats());
    }

    @Operation(summary = "Добавить жанр")
    @PostMapping("/genres")
    public ResponseEntity<Void> addGenre(@RequestBody GenreCreateDTO dto) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!userData.isAdmin()) {
            throw new SecurityException("Доступ только для администраторов");
        }

        adminService.addGenre(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}