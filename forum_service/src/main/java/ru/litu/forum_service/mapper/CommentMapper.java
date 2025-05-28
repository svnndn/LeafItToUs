package ru.litu.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.litu.forum_service.dto.comment.RequestCommentDto;
import ru.litu.forum_service.dto.comment.ResponseCommentDto;
import ru.litu.forum_service.entity.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    ResponseCommentDto toDto(Comment entity);
    Comment toEntity(RequestCommentDto dto);
}
