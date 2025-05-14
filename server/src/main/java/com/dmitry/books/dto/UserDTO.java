package com.dmitry.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    
    @Schema(description = "Ник пользователя")
    @JsonProperty("username")
    private String username;
    
    @Schema(description = "Идентификатор пользователя")
    @JsonProperty("id")
    private Long id;
    
    @Schema(description = "Является ли пользователь администратором")
    @JsonProperty("isAdmin")
    private boolean isAdmin;

}