package ru.litu.forum_service.dto.comment;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class RequestCommentDto  {
    private String textContent;
    private Long authorId;
}
