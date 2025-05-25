package ru.litu.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.litu.forum_service.dto.PublicationDto;
import ru.litu.forum_service.entity.Publication;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicationMapper {
    PublicationDto toDto(Publication entity);
    Publication toEntity(PublicationDto dto);
}

