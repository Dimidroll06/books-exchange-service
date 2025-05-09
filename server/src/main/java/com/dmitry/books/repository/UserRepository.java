package com.dmitry.books.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.dmitry.books.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserDTO getUserByUsername(String username) {
        String url = "http://localhost:8080/user?username=" + username;
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return new UserDTO(
                jsonNode.get("username").asText(),
                jsonNode.get("id").asLong(),
                jsonNode.get("is_admin").asBoolean()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse user data", e);
        }
    }

    public UserDTO getUserById(Long id) {
        String url = "http://localhost:8080/user/id?id=" + id;
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return new UserDTO(
                jsonNode.get("username").asText(),
                jsonNode.get("id").asLong(),
                jsonNode.get("is_admin").asBoolean()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse user data", e);
        }
    }
}

