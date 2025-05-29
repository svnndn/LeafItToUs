package ru.litu.forum_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litu.forum_service.dto.publication.RequestPublicationDto;
import ru.litu.forum_service.dto.publication.ResponsePublicationDto;
import ru.litu.forum_service.service.PublicationService;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping
    public ResponseEntity<ResponsePublicationDto> create(@RequestBody RequestPublicationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicationService.create(dto));
    }

    @GetMapping("/all-time")
    public ResponseEntity<List<ResponsePublicationDto>> getAll() {
        return ResponseEntity.ok(publicationService.findAll());
    }

    @GetMapping("/last-day")
    public ResponseEntity<List<ResponsePublicationDto>> getPublicationsLastDay() {
        return ResponseEntity.ok(publicationService.getPublicationsSince(Duration.ofDays(1)));
    }

    @GetMapping("/last-week")
    public ResponseEntity<List<ResponsePublicationDto>> getPublicationsLastWeek() {
        return ResponseEntity.ok(publicationService.getPublicationsSince(Duration.ofDays(7)));
    }

    @GetMapping("/last-month")
    public ResponseEntity<List<ResponsePublicationDto>> getPublicationsLastMonth() {
        return ResponseEntity.ok(publicationService.getPublicationsSince(Duration.ofDays(30)));
    }

    @GetMapping("/last-year")
    public ResponseEntity<List<ResponsePublicationDto>> getPublicationsLastYear() {
        return ResponseEntity.ok(publicationService.getPublicationsSince(Duration.ofDays(365)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) throws AccessDeniedException {
        publicationService.deleteById(id, userId);
        return ResponseEntity.noContent().build();
    }
}
