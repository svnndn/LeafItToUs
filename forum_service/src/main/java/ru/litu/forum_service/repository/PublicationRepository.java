package ru.litu.forum_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litu.forum_service.entity.Publication;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findAllByAuthorId(Long authorId);
    List<Publication> findByCreatedOnAfter(LocalDateTime createdOn);
}
