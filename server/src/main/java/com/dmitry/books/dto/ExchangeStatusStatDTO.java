package com.dmitry.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description="DTO для статистики по обменам")
public class ExchangeStatusStatDTO {
    private int status;
    private long count;
}