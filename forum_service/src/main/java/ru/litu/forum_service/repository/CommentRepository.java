package ru.litu.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litu.forum_service.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPublicationId(Long publicationId);
}
