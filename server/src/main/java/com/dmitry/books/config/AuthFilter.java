package com.dmitry.books.config;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AuthFilter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            String validateUrl = System.getenv().getOrDefault("AUTH_SERVER_URL", "http://auth:8080") + "/validate";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String userDataJson = restTemplate.exchange(
                    validateUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            ).getBody();

            UserData userData = objectMapper.readValue(userDataJson, UserData.class);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userData.getUsername(),
                    null,
                    null
            );
            authentication.setDetails(userData);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RestClientException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserData {
        private String username;
        private Long userId;
        private boolean isAdmin;
    }
}