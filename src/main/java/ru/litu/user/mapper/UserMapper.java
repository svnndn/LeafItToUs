package ru.litu.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.litu.user.dto.NewUserDto;
import ru.litu.user.dto.USerDto;
import ru.litu.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    USerDto userToUserDto(User user);

    List<USerDto> listUserToListUserDto(List<User> user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User newUserRequestDtoToUser(NewUserDto newUserDto);
}
