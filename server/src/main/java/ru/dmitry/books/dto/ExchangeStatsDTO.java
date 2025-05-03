package ru.dmitry.books.dto;

import lombok.Data;

@Data
public class ExchangeStatsDTO {
    private long totalExchanges;
    private long pendingExchanges;
    private long proceedExchanges;
    private long completedExchanges;
    private long deniedExchanges;
}