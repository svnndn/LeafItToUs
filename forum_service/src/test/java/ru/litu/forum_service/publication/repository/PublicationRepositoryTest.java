package ru.litu.forum_service.publication.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.forum_service.TestLoggerExtension;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.repository.PublicationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(TestLoggerExtension.class)
class PublicationRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(PublicationRepositoryTest.class);

    @Autowired
    private PublicationRepository publicationRepository;

    private Publication createPublication(String text, Long authorId, LocalDateTime createdOn) {
        Publication publication = new Publication();
        publication.setTextContent(text);
        publication.setAuthorId(authorId);
        publication.setCreatedOn(createdOn);
        return publicationRepository.save(publication);
    }

    @Test
    @DisplayName("findAllByAuthorId: должен вернуть публикации указанного автора")
    void findAllByAuthorId_ShouldReturnPublications() {
        log.info("Запуск теста findAllByAuthorId_ShouldReturnPublications");

        createPublication("Пост автора 1", 1L, LocalDateTime.now());
        createPublication("Пост автора 1 - второй", 1L, LocalDateTime.now());
        createPublication("Пост другого автора", 2L, LocalDateTime.now());

        List<Publication> result = publicationRepository.findAllByAuthorId(1L);
        log.info("Найдено публикаций автора 1: {}", result.size());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Publication::getTextContent)
                .contains("Пост автора 1", "Пост автора 1 - второй");
    }

    @Test
    @DisplayName("findAllByAuthorId: должен вернуть пустой список, если публикаций нет")
    void findAllByAuthorId_ShouldReturnEmptyList() {
        log.info("Запуск теста findAllByAuthorId_ShouldReturnEmptyList");

        List<Publication> result = publicationRepository.findAllByAuthorId(999L);
        assertThat(result).isEmpty();
        log.info("Найдено публикаций: {}", result.size());
    }

    @Test
    @DisplayName("findByCreatedOnAfter: должен вернуть публикации за последние N часов")
    void findByCreatedOnAfter_ShouldReturnRecentPublications() {
        log.info("Запуск теста findByCreatedOnAfter_ShouldReturnRecentPublications");

        LocalDateTime now = LocalDateTime.now();
        createPublication("Старый пост", 1L, now.minusDays(2));
        createPublication("Новый пост", 1L, now.minusMinutes(10));

        List<Publication> result = publicationRepository.findByCreatedOnAfter(now.minusHours(1));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTextContent()).isEqualTo("Новый пост");
    }

    @Test
    @DisplayName("findByCreatedOnAfter: должен вернуть пустой список, если публикаций нет")
    void findByCreatedOnAfter_ShouldReturnEmptyList() {
        log.info("Запуск теста findByCreatedOnAfter_ShouldReturnEmptyList");

        LocalDateTime now = LocalDateTime.now();
        createPublication("Старый пост", 1L, now.minusDays(1));

        List<Publication> result = publicationRepository.findByCreatedOnAfter(now.minusHours(1));
        assertThat(result).isEmpty();
    }
}
