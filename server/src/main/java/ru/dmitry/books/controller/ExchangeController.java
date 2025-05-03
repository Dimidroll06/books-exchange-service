package ru.dmitry.books.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.dmitry.books.dto.ExchangeCreateDTO;
import ru.dmitry.books.dto.ExchangeResponseDTO;
import ru.dmitry.books.dto.ExchangeUpdateStatusDTO;
import ru.dmitry.books.service.ExchangeService;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @Operation(summary = "Создать сделку")
    @PostMapping
    public ResponseEntity<ExchangeResponseDTO> createExchange(
            @Valid @RequestBody ExchangeCreateDTO dto,
            HttpServletRequest request) {
        Long fromUserId = (Long) request.getAttribute("userId");
        ExchangeResponseDTO exchange = exchangeService.createExchange(fromUserId, dto);
        return ResponseEntity.ok(exchange);
    }

    @Operation(summary = "Изменить статус сделки")
    @PatchMapping("/{exchangeId}/status")
    public ResponseEntity<ExchangeResponseDTO> updateExchangeStatus(
            @Parameter(description = "Идентификатор сделки", example = "1")
            @PathVariable Long exchangeId,

            @Valid @RequestBody ExchangeUpdateStatusDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ExchangeResponseDTO updatedExchange = exchangeService.updateExchangeStatus(exchangeId, userId, dto);
        return ResponseEntity.ok(updatedExchange);
    }

    @Operation(summary = "Получить все свои сделки")
    @GetMapping
    public ResponseEntity<List<ExchangeResponseDTO>> getUserExchanges(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ExchangeResponseDTO> exchanges = exchangeService.getUserExchanges(userId);
        return ResponseEntity.ok(exchanges);
    }
}