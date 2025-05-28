package ru.litu.forum_service.dto.comment;

import lombok.*;
import ru.litu.forum_service.entity.Publication;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ResponseCommentDto {
    private Long id;
    private String textContent;
    private LocalDateTime createdOn;
    private Long authorId;
}
