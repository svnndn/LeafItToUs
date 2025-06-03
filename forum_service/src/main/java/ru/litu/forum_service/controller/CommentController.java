package ru.litu.forum_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litu.forum_service.dto.comment.RequestCommentDto;
import ru.litu.forum_service.dto.comment.ResponseCommentDto;
import ru.litu.forum_service.dto.user.UserDto;
import ru.litu.forum_service.service.CommentService;
import ru.litu.forum_service.service.rest.UserServiceClient;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/by-publication/{publicationId}")
    public ResponseEntity<ResponseCommentDto> createByPublication(@RequestBody RequestCommentDto dto,
                                                                  @PathVariable Long publicationId) {
        UserDto author = userServiceClient.getUserById(dto.getAuthorId());
        System.out.println("Creating comment by " + author.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(dto, publicationId));
    }

    @GetMapping("/by-publication/{publicationId}")
    public ResponseEntity<List<ResponseCommentDto>> getByPublication(@PathVariable Long publicationId) {
        return ResponseEntity.ok(commentService.getAllByPublicationId(publicationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) throws AccessDeniedException {
        commentService.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }
}

