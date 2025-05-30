package ru.litu.main_service.user.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.dto.UpdateUserDto;
import ru.litu.main_service.user.dto.UserDto;
import ru.litu.main_service.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "name")
    UserDto userToUserDto(User user);

    List<UserDto> listUserToListUserDto(List<User> user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User newUserRequestDtoToUser(NewUserDto newUserDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserDto dto, @MappingTarget User user);
}
