package ru.litu.forum_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litu.forum_service.dto.comment.RequestCommentDto;
import ru.litu.forum_service.dto.comment.ResponseCommentDto;
import ru.litu.forum_service.entity.Comment;
import ru.litu.forum_service.entity.Publication;
import ru.litu.forum_service.mapper.CommentMapper;
import ru.litu.forum_service.repository.CommentRepository;
import ru.litu.forum_service.repository.PublicationRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PublicationRepository publicationRepository;
    private final CommentMapper commentMapper;

    public ResponseCommentDto create(RequestCommentDto dto, Long publicationId) {

        System.out.println(dto);

        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found"));

        Comment entity = commentMapper.toEntity(dto);
        entity.setCreatedOn(LocalDateTime.now());

        System.out.println(entity);

        entity.setPublication(publication);

        Comment saved = commentRepository.save(entity);
        return commentMapper.toDto(saved);
    }

    public List<ResponseCommentDto> getAllByPublicationId(Long publicationId) {
        return commentRepository.findAllByPublicationId(publicationId).stream()
                .filter(comment -> comment.getCreatedOn() != null)
                .sorted(Comparator.comparing(Comment::getCreatedOn).reversed())
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
