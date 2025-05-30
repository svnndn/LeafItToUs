package ru.litu.main_service.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.dto.UpdateUserDto;
import ru.litu.main_service.user.dto.UserDto;
import ru.litu.main_service.user.mapper.UserMapper;
import ru.litu.main_service.user.model.User;
import ru.litu.main_service.user.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("POST /users - Creating user: {}", newUserDto);
        User user = userService.add(newUserDto);
        return userMapper.userToUserDto(user);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Get user", id);
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody @Valid UpdateUserDto updateUserDto) {
        log.info("PATCH /users/{} - Update user: {}", id, updateUserDto);
        User updatedUser = userService.update(id, updateUserDto);
        return userMapper.userToUserDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Delete user", id);
        userService.delete(id);
    }
}
