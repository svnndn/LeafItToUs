package ru.litu.forum_service.service.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.litu.forum_service.dto.user.UserDto;

@Service
public class UserServiceClient {
    private final WebClient webClient;

    public UserServiceClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public UserDto getUserById(Long userId) {
        System.out.println("Fetching user with ID: " + userId);
        try {
            UserDto user = webClient.get()
                    .uri("/users/{id}", userId)
                    .exchangeToMono(response -> {
                        return response.bodyToMono(UserDto.class);
                    })
                    .block();
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user", e);
        }
    }
}