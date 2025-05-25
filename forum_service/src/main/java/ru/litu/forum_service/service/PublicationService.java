package ru.litu.forum_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litu.forum_service.dto.PublicationDto;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.mapper.PublicationMapper;
import ru.litu.forum_service.repository.PublicationRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;

    public PublicationDto create(PublicationDto dto) {
        Publication entity = publicationMapper.toEntity(dto);
        entity.setCreatedOn(LocalDateTime.now());

        Publication saved = publicationRepository.save(entity);
        return publicationMapper.toDto(saved);
    }

    public List<PublicationDto> findAll() {
        return publicationRepository.findAll().stream()
                .map(publicationMapper::toDto)
                .toList();
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
