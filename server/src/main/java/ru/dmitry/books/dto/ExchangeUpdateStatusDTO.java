package ru.dmitry.books.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.dmitry.books.model.ExchangeEntity.ExchangeStatus;

@Data
public class ExchangeUpdateStatusDTO {

    @NotNull
    private ExchangeStatus status;
}