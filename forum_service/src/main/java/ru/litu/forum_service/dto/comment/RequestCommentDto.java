package ru.litu.forum_service.dto.comment;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestCommentDto  {
    private String textContent;
    private Long authorId;

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
