package ru.litu.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.litu.user.dto.NewUserDto;
import ru.litu.user.dto.UserDto;
import ru.litu.user.mapper.UserMapper;
import ru.litu.user.model.User;
import ru.litu.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addAdminUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("Calling POST: /admin/users with 'newUserRequestDto': {}", newUserDto.toString());
        User user = userService.addAdminUser(userMapper.newUserRequestDtoToUser(newUserDto));
        return userMapper.userToUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAdminUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                       @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {

        log.info("Calling GET: /admin/users with 'ids': {}, 'from': {}, 'size': {}", ids, from, size);
        return userMapper.listUserToListUserDto(userService.getByIds(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminUser(@PathVariable Long userId) {
        log.info("Calling DELETE: /admin/users/{userId} with 'userId': {}", userId);
        userService.delete(userId);
    }
}
