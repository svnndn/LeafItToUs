package ru.litu.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.litu.forum_service.dto.CommentDto;
import ru.litu.forum_service.entity.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PublicationMapper.class})
public interface CommentMapper {
    CommentDto toDto(Comment entity);
    Comment toEntity(CommentDto dto);
}
