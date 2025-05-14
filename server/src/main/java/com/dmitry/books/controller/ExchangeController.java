package com.dmitry.books.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmitry.books.config.AuthFilter;
import com.dmitry.books.dto.ExchangeRequestDTO;
import com.dmitry.books.dto.ExchangeResponseDTO;
import com.dmitry.books.dto.PageDTO;
import com.dmitry.books.service.ExchangeService;
import com.dmitry.books.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/exchange")
public class ExchangeController {

    @Autowired
    private final ExchangeService exchangeService;

    @Operation(summary = "Получить обмены по ID отправителя")
    @GetMapping("/by-sender/{id}")
    public ResponseEntity<PageDTO<ExchangeResponseDTO>> getExchangesBySenderId(
            @Parameter(description = "ID Отправителя") @PathVariable Long id,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ExchangeResponseDTO> exchanges = exchangeService.getExchangeBySenderId(id, pageable);
        return ResponseEntity.ok(exchanges);
    }

    @Operation(summary = "Получить обмены по ID получателя")
    @GetMapping("/by-getter/{id}")
    public ResponseEntity<PageDTO<ExchangeResponseDTO>> getExchangesByGetterId(
            @Parameter(description = "ID Получателя") @PathVariable Long id,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ExchangeResponseDTO> exchanges = exchangeService.getExchangeByGetterId(id, pageable);
        return ResponseEntity.ok(exchanges);
    }

    @Operation(summary = "Получить обмены пользователя")
    @GetMapping("/by-user/{id}")
    public ResponseEntity<PageDTO<ExchangeResponseDTO>> getExchangesByUserId(
            @Parameter(description = "ID Пользователя") @PathVariable Long id,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ExchangeResponseDTO> exchanges = exchangeService.getExchangeByUserId(id, pageable);
        return ResponseEntity.ok(exchanges);
    }

    @Operation(summary = "Получить мои обмены")
    @GetMapping("/my")
    public ResponseEntity<PageDTO<ExchangeResponseDTO>> getExchangesByUserId(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
            
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ExchangeResponseDTO> exchanges = exchangeService.getExchangeByUserId(userData.getUserId(), pageable);
        return ResponseEntity.ok(exchanges);
    }

    @Operation(summary = "Получить обмен по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ExchangeResponseDTO> getExchangeById(
            @Parameter(description = "ID обмена") @PathVariable Long id) {
        Optional<ExchangeResponseDTO> exchange = exchangeService.getExchangeById(id);
        return exchange.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Создать новый обмен")
    @PostMapping
    public ResponseEntity<Void> createNewExchange(
            @Parameter(description = "Данные для создания обмена") @RequestBody ExchangeRequestDTO exchange) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        exchangeService.createExchange(exchange, userData.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Отклонить обмен")
    @PutMapping("/reject/{id}")
    public ResponseEntity<Void> rejectExchange(
            @Parameter(description = "ID обмена") @PathVariable Long id) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        exchangeService.rejectExchange(id, userData.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Отправить обмен")
    @PutMapping("/send/{id}")
    public ResponseEntity<Void> sendExchange(
            @Parameter(description = "ID обмена") @PathVariable Long id) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        exchangeService.sendExchange(id, userData.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Принять обмен")
    @PutMapping("/accept/{id}")
    public ResponseEntity<Void> acceptExchange(
            @Parameter(description = "ID обмена") @PathVariable Long id) {
        AuthFilter.UserData userData = SecurityUtils.getCurrentUser();
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        exchangeService.acceptExchange(id, userData.getUserId());
        return ResponseEntity.ok().build();
    }

}