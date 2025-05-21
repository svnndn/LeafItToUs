package user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import user.dto.NewUserDto;
import user.dto.USerDto;
import user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    USerDto userToUserDto(User user);

    List<USerDto> listUserToListUserDto(List<User> user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User newUserRequestDtoToUser(NewUserDto newUserDto);
}
