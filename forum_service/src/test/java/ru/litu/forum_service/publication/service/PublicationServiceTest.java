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

        when(publicationMapper.toEntity(dto)).thenReturn(entity);
        when(publicationRepository.save(any(Publication.class))).thenReturn(saved);
        when(publicationMapper.toDto(saved)).thenReturn(response);

        ResponsePublicationDto result = publicationService.create(dto);

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

        when(publicationRepository.findAll()).thenReturn(List.of(p1, p2));
        when(publicationMapper.toDtoList(anyList()))
                .thenAnswer(invocation -> {
                    List<Publication> pubs = invocation.getArgument(0);
                    return pubs.stream()
                            .map(p -> new ResponsePublicationDto(p.getId(), p.getTextContent(), p.getCreatedOn(), p.getAuthorId()))
                            .toList();
                });

        List<ResponsePublicationDto> result = publicationService.findAll();

        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedOn().isAfter(result.get(1).getCreatedOn()));
    }

    @Test
    @DisplayName("getPublicationsSince: должен вернуть публикации за заданный период")
    void getPublicationsSince_ShouldReturnRecent() {
        LocalDateTime now = LocalDateTime.now();
        Publication recent = new Publication(1L, "Recent post", now.minusMinutes(30), 1L, List.of());

        when(publicationRepository.findByCreatedOnAfter(any(LocalDateTime.class))).thenReturn(List.of(recent));
        when(publicationMapper.toDtoList(anyList()))
                .thenAnswer(invocation -> {
                    List<Publication> pubs = invocation.getArgument(0);
                    return pubs.stream()
                            .map(p -> new ResponsePublicationDto(p.getId(), p.getTextContent(), p.getCreatedOn(), p.getAuthorId()))
                            .toList();
                });

        List<ResponsePublicationDto> result = publicationService.getPublicationsSince(Duration.ofHours(1));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getCreatedOn().isAfter(now.minusHours(1)));
    }

    @Test
    @DisplayName("deleteById: должен удалить публикацию, если автор совпадает")
    void deleteById_ShouldDeleteIfAuthorMatches() throws AccessDeniedException {
        Publication pub = new Publication(1L, "text", LocalDateTime.now(), 42L, List.of());
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(pub));

        publicationService.deleteById(1L, 42L);

        verify(publicationRepository).delete(pub);
    }

    @Test
    @DisplayName("deleteById: должен выбросить AccessDeniedException при несовпадении автора")
    void deleteById_ShouldThrowAccessDenied() {
        Publication pub = new Publication(1L, "text", LocalDateTime.now(), 42L, List.of());
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(pub));

        assertThrows(AccessDeniedException.class, () -> publicationService.deleteById(1L, 99L));
    }

    @Test
    @DisplayName("deleteById: должен выбросить EntityNotFoundException при отсутствии публикации")
    void deleteById_ShouldThrowEntityNotFound() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> publicationService.deleteById(1L, 42L));
    }
}
