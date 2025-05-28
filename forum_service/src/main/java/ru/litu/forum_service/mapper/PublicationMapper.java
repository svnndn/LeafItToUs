package ru.litu.forum_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.litu.forum_service.dto.publication.RequestPublicationDto;
import ru.litu.forum_service.dto.publication.ResponsePublicationDto;
import ru.litu.forum_service.entity.Publication;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicationMapper {
    ResponsePublicationDto toDto(Publication entity);
    Publication toEntity(RequestPublicationDto dto);

    List<ResponsePublicationDto> toDtoList(List<Publication> publications);
}

