package ru.litu.forum_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litu.forum_service.dto.CommentDto;
import ru.litu.forum_service.entity.Comment;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.mapper.CommentMapper;
import ru.litu.forum_service.repository.CommentRepository;
import ru.litu.forum_service.repository.PublicationRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PublicationRepository publicationRepository;
    private final CommentMapper commentMapper;

    public CommentDto create(CommentDto dto) {
        Publication publication = publicationRepository.findById(dto.getPublication().getId())
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));

        Comment entity = commentMapper.toEntity(dto);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setPublication(publication);

        Comment saved = commentRepository.save(entity);
        return commentMapper.toDto(saved);
    }

    public List<CommentDto> getAllByPublicationId(Long publicationId) {
        return commentRepository.findAllByPublicationId(publicationId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteById(Long id, Long requesterId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getAuthorId().equals(requesterId)) {
            throw new AccessDeniedException("You can delete only your own comments");
        }

        commentRepository.delete(comment);
    }
}
