package ru.litu.forum_service.comment.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.litu.forum_service.TestLoggerExtension;
import ru.litu.forum_service.dto.comment.RequestCommentDto;
import ru.litu.forum_service.dto.comment.ResponseCommentDto;
import ru.litu.forum_service.entity.Comment;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.mapper.CommentMapper;
import ru.litu.forum_service.repository.CommentRepository;
import ru.litu.forum_service.repository.PublicationRepository;
import ru.litu.forum_service.service.CommentService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestLoggerExtension.class)
class CommentServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceTest.class);

    @Mock private CommentRepository commentRepository;
    @Mock private PublicationRepository publicationRepository;
    @Mock private CommentMapper commentMapper;

    @InjectMocks private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Инициализация моков и сервиса завершена.");
    }

    @Test
    @DisplayName("create: должен создать комментарий")
    void create_ShouldSaveComment() {
        log.info("Запуск теста create_ShouldSaveComment");

        RequestCommentDto dto = new RequestCommentDto("Test comment", 1L);
        Publication publication = new Publication();
        Comment comment = new Comment();
        comment.setTextContent(dto.getTextContent());
        comment.setAuthorId(dto.getAuthorId());

        Comment saved = new Comment(1L, dto.getTextContent(), LocalDateTime.now(), dto.getAuthorId(), publication);
        ResponseCommentDto response = new ResponseCommentDto(saved.getId(), saved.getTextContent(), saved.getCreatedOn(), saved.getAuthorId());

        log.info("Имитируем поведение зависимостей");
        when(publicationRepository.findById(10L)).thenReturn(Optional.of(publication));
        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(response);

        log.info("Попытка создать комментарий: {}", dto);
        ResponseCommentDto result = commentService.create(dto, 10L);
        log.info("Создан комментарий: {}", result);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        verify(commentRepository).save(any(Comment.class));
        log.info("Комментарий успешно создан и сохранён");
    }

    @Test
    @DisplayName("getAllByPublicationId: должен вернуть отсортированные комментарии")
    void getAllByPublicationId_ShouldReturnSortedList() {
        log.info("Запуск теста getAllByPublicationId_ShouldReturnSortedList");

        Publication publication = new Publication();
        Comment comment1 = new Comment(1L, "Comment1", LocalDateTime.now().minusMinutes(1), 1L, publication);
        Comment comment2 = new Comment(2L, "Comment2", LocalDateTime.now(), 2L, publication);

        log.info("Созданы тестовые комментарии: {}, {}", comment1, comment2);

        when(commentRepository.findAllByPublicationId(5L)).thenReturn(List.of(comment1, comment2));
        when(commentMapper.toDto(comment1)).thenReturn(new ResponseCommentDto(1L, "Comment1", comment1.getCreatedOn(), 1L));
        when(commentMapper.toDto(comment2)).thenReturn(new ResponseCommentDto(2L, "Comment2", comment2.getCreatedOn(), 2L));

        List<ResponseCommentDto> result = commentService.getAllByPublicationId(5L);

        log.info("Получены комментарии: {}", result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedOn().isAfter(result.get(1).getCreatedOn()));
        log.info("Комментарии корректно отсортированы по дате");
    }

    @Test
    @DisplayName("deleteById: должен удалить комментарий при совпадении автора")
    void deleteById_ShouldDeleteComment() throws AccessDeniedException {
        log.info("Запуск теста deleteById_ShouldDeleteComment");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthorId(42L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        log.info("Попытка удалить комментарий с ID={} от автора ID={}", comment.getId(), comment.getAuthorId());
        commentService.deleteById(1L, 42L);

        verify(commentRepository).delete(comment);
        log.info("Комментарий успешно удален");
    }

    @Test
    @DisplayName("deleteById: должен выбросить AccessDeniedException, если не автор")
    void deleteById_ShouldThrowAccessDenied_WhenNotAuthor() {
        log.info("Запуск теста deleteById_ShouldThrowAccessDenied_WhenNotAuthor");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthorId(42L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        log.info("Попытка удалить комментарий автором ID=99, комментарий создан автором ID={}", comment.getAuthorId());
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> commentService.deleteById(1L, 99L));
        log.warn("Удаление завершилось исключением: {}", ex.getMessage());
    }

    @Test
    @DisplayName("deleteById: должен выбросить EntityNotFoundException, если комментарий не найден")
    void deleteById_ShouldThrowEntityNotFound_WhenNotFound() {
        log.info("Запуск теста deleteById_ShouldThrowEntityNotFound_WhenNotFound");

        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        log.info("Попытка удалить несуществующий комментарий ID=999");
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> commentService.deleteById(999L, 42L));
        log.warn("Удаление завершилось исключением: {}", ex.getMessage());
    }
}
