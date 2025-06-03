package ru.litu.forum_service.publication.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.litu.forum_service.TestLoggerExtension;
import ru.litu.forum_service.dto.publication.RequestPublicationDto;
import ru.litu.forum_service.dto.publication.ResponsePublicationDto;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.mapper.PublicationMapper;
import ru.litu.forum_service.repository.PublicationRepository;
import ru.litu.forum_service.service.PublicationService;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestLoggerExtension.class)
class PublicationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PublicationServiceTest.class);

    @Mock private PublicationRepository publicationRepository;
    @Mock private PublicationMapper publicationMapper;

    @InjectMocks private PublicationService publicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("create: должен сохранить публикацию")
    void create_ShouldSavePublication() {
        RequestPublicationDto dto = new RequestPublicationDto("Test text", 1L);
        Publication entity = new Publication();
        entity.setTextContent(dto.getTextContent());
        entity.setAuthorId(dto.getAuthorId());

        Publication saved = new Publication(1L, "Test text", LocalDateTime.now(), 1L, List.of());
        ResponsePublicationDto response = new ResponsePublicationDto(1L, "Test text", saved.getCreatedOn(), 1L);

        log.info("Создаём публикацию с текстом='{}' и authorId={}", dto.getTextContent(), dto.getAuthorId());

        when(publicationMapper.toEntity(dto)).thenReturn(entity);
        when(publicationRepository.save(any(Publication.class))).thenReturn(saved);
        when(publicationMapper.toDto(saved)).thenReturn(response);

        ResponsePublicationDto result = publicationService.create(dto);

        log.info("Публикация сохранена с id={}, текст='{}'", result.getId(), result.getTextContent());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(saved.getTextContent(), result.getTextContent());
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    @DisplayName("findAll: должен вернуть публикации отсортированные по убыванию даты")
    void findAll_ShouldReturnSorted() {
        Publication p1 = new Publication(1L, "text1", LocalDateTime.now().minusHours(2), 1L, List.of());
        Publication p2 = new Publication(2L, "text2", LocalDateTime.now(), 2L, List.of());

        log.info("Мокаем findAll публикаций: {} и {}", p1.getId(), p2.getId());

        when(publicationRepository.findAll()).thenReturn(List.of(p1, p2));
        when(publicationMapper.toDtoList(anyList()))
                .thenAnswer(invocation -> {
                    List<Publication> pubs = invocation.getArgument(0);
                    log.info("Конвертация {} публикаций в DTO", pubs.size());
                    return pubs.stream()
                            .map(p -> new ResponsePublicationDto(p.getId(), p.getTextContent(), p.getCreatedOn(), p.getAuthorId()))
                            .toList();
                });

        List<ResponsePublicationDto> result = publicationService.findAll();

        log.info("Получено публикаций: {}", result.size());
        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedOn().isAfter(result.get(1).getCreatedOn()),
                "Публикации не отсортированы по убыванию даты");
    }

    @Test
    @DisplayName("getPublicationsSince: должен вернуть публикации за заданный период")
    void getPublicationsSince_ShouldReturnRecent() {
        LocalDateTime now = LocalDateTime.now();
        Publication recent = new Publication(1L, "Recent post", now.minusMinutes(30), 1L, List.of());

        log.info("Тестируем получение публикаций за последние 1 час");

        when(publicationRepository.findByCreatedOnAfter(any(LocalDateTime.class))).thenReturn(List.of(recent));
        when(publicationMapper.toDtoList(anyList()))
                .thenAnswer(invocation -> {
                    List<Publication> pubs = invocation.getArgument(0);
                    log.info("Конвертация {} публикаций в DTO", pubs.size());
                    return pubs.stream()
                            .map(p -> new ResponsePublicationDto(p.getId(), p.getTextContent(), p.getCreatedOn(), p.getAuthorId()))
                            .toList();
                });

        List<ResponsePublicationDto> result = publicationService.getPublicationsSince(Duration.ofHours(1));

        log.info("Получено публикаций: {}", result.size());
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCreatedOn().isAfter(now.minusHours(1)), "Публикация старше запрошенного периода");
    }

    @Test
    @DisplayName("deleteById: должен удалить публикацию, если автор совпадает")
    void deleteById_ShouldDeleteIfAuthorMatches() throws AccessDeniedException {
        Publication pub = new Publication(1L, "text", LocalDateTime.now(), 42L, List.of());

        log.info("Тест удаления публикации id={} авторId={}", pub.getId(), pub.getAuthorId());
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(pub));

        publicationService.deleteById(1L, 42L);

        verify(publicationRepository).delete(pub);
        log.info("Публикация id={} успешно удалена", pub.getId());
    }

    @Test
    @DisplayName("deleteById: должен выбросить AccessDeniedException при несовпадении автора")
    void deleteById_ShouldThrowAccessDenied() {
        Publication pub = new Publication(1L, "text", LocalDateTime.now(), 42L, List.of());

        log.info("Тест удаления публикации с неправильным автором, публикация id={}, авторId={}", pub.getId(), pub.getAuthorId());
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(pub));

        AccessDeniedException thrown = assertThrows(AccessDeniedException.class,
                () -> publicationService.deleteById(1L, 99L));

        log.info("Исключение AccessDeniedException поймано: {}", thrown.getMessage());
    }

    @Test
    @DisplayName("deleteById: должен выбросить EntityNotFoundException при отсутствии публикации")
    void deleteById_ShouldThrowEntityNotFound() {
        log.info("Тест удаления публикации, которой не существует (id=1)");
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> publicationService.deleteById(1L, 42L));

        log.info("Исключение EntityNotFoundException поймано: {}", thrown.getMessage());
    }
}
