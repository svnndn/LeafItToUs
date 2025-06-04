package ru.litu.forum_service.publication.controller;

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
import ru.litu.forum_service.dto.publication.RequestPublicationDto;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.repository.PublicationRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PublicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PublicationRepository publicationRepository;

    private Publication publicationRecent;
    private Publication publicationOld;

    @BeforeEach
    void setUp() {
        publicationRepository.deleteAll();

        publicationRecent = new Publication();
        publicationRecent.setTextContent("Recent publication");
        publicationRecent.setAuthorId(1L);
        publicationRecent.setCreatedOn(LocalDateTime.now());

        publicationOld = new Publication();
        publicationOld.setTextContent("Old publication");
        publicationOld.setAuthorId(2L);
        publicationOld.setCreatedOn(LocalDateTime.now().minusDays(10));

        publicationRepository.save(publicationRecent);
        publicationRepository.save(publicationOld);
    }

    @Test
    @DisplayName("POST /publications — создание публикации")
    void createPublication_ShouldReturnCreatedDto() throws Exception {
        RequestPublicationDto dto = new RequestPublicationDto();
        dto.setTextContent("Новая публикация");
        dto.setAuthorId(99L);

        mockMvc.perform(post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /publications/all-time — все публикации")
    void getAllPublications_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/publications/all-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /publications/last-day — публикации за последний день")
    void getLastDayPublications_ShouldReturnRecent() throws Exception {
        mockMvc.perform(get("/publications/last-day")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /publications/last-week — публикации за последнюю неделю")
    void getLastWeekPublications_ShouldReturnRecentOnly() throws Exception {
        mockMvc.perform(get("/publications/last-week")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /publications/last-month — публикации за последний месяц")
    void getLastMonthPublications_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/publications/last-month")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /publications/last-year — публикации за последний год")
    void getLastYearPublications_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/publications/last-year")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /publications/{id} — удаление публикации")
    void deletePublication_ShouldReturnNoContent() throws Exception {
        Long idToDelete = publicationRecent.getId();
        Long authorId = publicationRecent.getAuthorId();

        mockMvc.perform(delete("/publications/{id}?userId={userId}", idToDelete, authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(publicationRepository.findById(idToDelete)).isEmpty();
    }
}
