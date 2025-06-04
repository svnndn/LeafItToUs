package ru.litu.forum_service.comment.repository;

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
import ru.litu.forum_service.entity.Comment;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.repository.CommentRepository;
import ru.litu.forum_service.repository.PublicationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(TestLoggerExtension.class)
class CommentRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(CommentRepositoryTest.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    private Comment createComment(String content, Long authorId, Publication publication) {
        Comment comment = new Comment();
        comment.setTextContent(content);
        comment.setAuthorId(authorId);
        comment.setCreatedOn(LocalDateTime.now());
        comment.setPublication(publication);
        return comment;
    }

    private Publication createPublication() {
        Publication publication = new Publication();
        publication.setTextContent("Публикация для теста комментариев");
        publication.setCreatedOn(LocalDateTime.now());
        publication.setAuthorId(1L);
        return publicationRepository.save(publication);
    }

    @Test
    @DisplayName("findAllByPublicationId: должен вернуть комментарии по ID публикации")
    void findAllByPublicationId_ShouldReturnComments() {
        log.info("Запуск теста findAllByPublicationId_ShouldReturnComments");

        Publication publication = createPublication();
        log.info("Создана публикация с ID: {}", publication.getId());

        Comment comment1 = createComment("Комментарий 1", 101L, publication);
        Comment comment2 = createComment("Комментарий 2", 102L, publication);
        commentRepository.saveAll(List.of(comment1, comment2));
        log.info("Сохранены комментарии для публикации ID={}", publication.getId());

        List<Comment> result = commentRepository.findAllByPublicationId(publication.getId());
        log.info("Найдено комментариев: {}", result.size());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Comment::getTextContent)
                .containsExactlyInAnyOrder("Комментарий 1", "Комментарий 2");

        log.info("Тест успешно завершён: комментарии найдены по publicationId");
    }


    @Test
    @DisplayName("findAllByPublicationId: должен вернуть пустой список, если комментариев нет")
    void findAllByPublicationId_ShouldReturnEmptyList() {
        log.info("Запуск теста findAllByPublicationId_ShouldReturnEmptyList");

        List<Comment> result = commentRepository.findAllByPublicationId(999L);
        log.info("Найдено комментариев: {}", result.size());

        assertThat(result).isEmpty();
        log.info("Тест успешно завершён: комментарии не найдены для несуществующего publicationId");
    }
}
