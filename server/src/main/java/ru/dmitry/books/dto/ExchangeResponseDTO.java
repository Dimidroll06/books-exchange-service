package ru.dmitry.books.dto;

import lombok.Data;
import ru.dmitry.books.model.ExchangeEntity.ExchangeStatus;

@Data
public class ExchangeResponseDTO {

    private Long id;
    private Long bookCopyId;
    private Long fromUserId;
    private Long toUserId;
    private ExchangeStatus status;
    private String location;
    private Long createdAt;
    private Long updatedAt;
}