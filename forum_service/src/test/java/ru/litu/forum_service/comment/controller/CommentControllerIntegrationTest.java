package ru.litu.forum_service.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.litu.forum_service.dto.comment.RequestCommentDto;
import ru.litu.forum_service.entity.Comment;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.repository.CommentRepository;
import ru.litu.forum_service.repository.PublicationRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    private Publication publication;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        publicationRepository.deleteAll();

        publication = new Publication();
        publication.setTextContent("Тестовая публикация");
        publication.setAuthorId(1L);
        publication.setCreatedOn(LocalDateTime.now());
        publication = publicationRepository.saveAndFlush(publication);

        Comment comment = new Comment();
        comment.setTextContent("Комментарий к публикации");
        comment.setAuthorId(2L);
        comment.setCreatedOn(LocalDateTime.now());
        comment.setPublication(publication);
        commentRepository.saveAndFlush(comment);
    }

    @Test
    @DisplayName("POST /comments/by-publication/{publicationId} — создание комментария")
    void shouldCreateComment() throws Exception {
        RequestCommentDto dto = new RequestCommentDto();
        dto.setTextContent("Новый комментарий");
        dto.setAuthorId(3L);

        mockMvc.perform(post("/comments/by-publication/{publicationId}", publication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /comments/by-publication/{publicationId} — получение всех комментариев публикации")
    void shouldReturnCommentsByPublication() throws Exception {
        mockMvc.perform(get("/comments/by-publication/{publicationId}", publication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("DELETE /comments/{id} — удаление комментария")
    void shouldDeleteComment() throws Exception {
        Comment commentToDelete = new Comment();
        commentToDelete.setTextContent("Комментарий на удаление");
        commentToDelete.setAuthorId(5L);
        commentToDelete.setCreatedOn(LocalDateTime.now());
        commentToDelete.setPublication(publication);
        commentToDelete = commentRepository.saveAndFlush(commentToDelete);

        Long idToDelete = commentToDelete.getId();
        Long userId = commentToDelete.getAuthorId();

        mockMvc.perform(delete("/comments/{id}?userId={userId}", idToDelete, userId))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.findById(idToDelete)).isEmpty();
    }
}
