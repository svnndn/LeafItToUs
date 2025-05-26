package ru.litu.forum_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litu.forum_service.dto.CommentDto;
import ru.litu.forum_service.service.CommentService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody CommentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(dto));
    }

    @GetMapping("/by-publication/{publicationId}")
    public ResponseEntity<List<CommentDto>> getByPublication(@PathVariable Long publicationId) {
        return ResponseEntity.ok(commentService.getAllByPublicationId(publicationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) throws AccessDeniedException {
        commentService.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }
}

