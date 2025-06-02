package ru.litu.forum_service.dto.publication;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class RequestPublicationDto {
    private String textContent;
    private Long authorId;
}
