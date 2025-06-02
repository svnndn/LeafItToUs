package ru.litu.main_service.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.dto.UpdateUserDto;
import ru.litu.main_service.user.dto.UserDto;
import ru.litu.main_service.user.model.User;


import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> getAll();

    UserDto getById(Long id);

    List<User> getByIds(List<Long> ids, Integer from, Integer size);

    User addAdminUser(User user);

    void delete(Long userId);

    List<User> getUsersWithIdBiggerThan(Long idMin);

    User add(NewUserDto user);

    User loadUserByUsername(String username) throws UsernameNotFoundException;

    User update(Long id, UpdateUserDto dto);

}
