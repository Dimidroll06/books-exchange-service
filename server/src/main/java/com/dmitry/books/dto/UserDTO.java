package com.dmitry.books.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private Long id;
    private boolean isAdmin;

    public UserDTO(String username, long id, boolean isAdmin) {
        this.username = username;
        this.id = id;
        this.isAdmin = isAdmin;
    }
}