package ru.litu.forum_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litu.forum_service.dto.PublicationDto;
import ru.litu.forum_service.service.PublicationService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping
    public ResponseEntity<PublicationDto> create(@RequestBody PublicationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicationService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<PublicationDto>> getAll() {
        return ResponseEntity.ok(publicationService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) throws AccessDeniedException {
        publicationService.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }
}
