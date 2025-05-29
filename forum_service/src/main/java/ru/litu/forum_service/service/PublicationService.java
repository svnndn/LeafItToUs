package ru.litu.forum_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litu.forum_service.dto.publication.RequestPublicationDto;
import ru.litu.forum_service.dto.publication.ResponsePublicationDto;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.mapper.PublicationMapper;
import ru.litu.forum_service.repository.PublicationRepository;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;

    public ResponsePublicationDto create(RequestPublicationDto dto) {
        // d
        System.out.println(dto);

        Publication entity = publicationMapper.toEntity(dto);

        System.out.println(entity);

        entity.setCreatedOn(LocalDateTime.now());

        Publication saved = publicationRepository.save(entity);
        return publicationMapper.toDto(saved);
    }

    public List<ResponsePublicationDto> findAll() {
        List<Publication> publications = publicationRepository.findAll().stream()
                .sorted(Comparator.comparing(Publication::getCreatedOn).reversed())
                .toList();
        return publicationMapper.toDtoList(publications);
    }

    public List<ResponsePublicationDto> getPublicationsSince(Duration duration) {
        LocalDateTime since = LocalDateTime.now().minus(duration);
        List<Publication> publications = publicationRepository.findByCreatedOnAfter(since);
        return publicationMapper.toDtoList(
                publications.stream()
                        .sorted(Comparator.comparing(Publication::getCreatedOn).reversed())
                        .toList()
        );
    }


    public void deleteById(Long id, Long requesterId) throws AccessDeniedException {
        Publication pub = publicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));

        if (!pub.getAuthorId().equals(requesterId)) {
            throw new AccessDeniedException("You can delete only your own publications");
        }

        publicationRepository.delete(pub);
    }
}
