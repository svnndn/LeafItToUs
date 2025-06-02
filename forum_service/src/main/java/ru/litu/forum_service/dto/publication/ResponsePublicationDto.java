package ru.litu.forum_service.dto.publication;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ResponsePublicationDto {
    private Long id;
    private String textContent;
    private LocalDateTime createdOn;
    private Long authorId;
}
