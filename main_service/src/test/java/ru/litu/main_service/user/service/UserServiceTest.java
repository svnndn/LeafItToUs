package ru.litu.main_service.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.litu.main_service.TestLoggerExtension;
import ru.litu.main_service.exception.ObjectNotFoundException;
import ru.litu.main_service.exception.SQLConstraintViolationException;
import ru.litu.main_service.user.dto.NewUserDto;
import ru.litu.main_service.user.dto.UpdateUserDto;
import ru.litu.main_service.user.dto.UserDto;
import ru.litu.main_service.user.mapper.UserMapper;
import ru.litu.main_service.user.model.Role;
import ru.litu.main_service.user.model.User;
import ru.litu.main_service.user.repository.RoleRepository;
import ru.litu.main_service.user.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestLoggerExtension.class)
class UserServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Инициализация моков и сервиса завершена.");
    }

    @Test
    @DisplayName("add_ShouldCreateUser: должен создать нового пользователя")
    void add_ShouldCreateUser() throws SQLConstraintViolationException {
        log.info("Запуск теста add_ShouldCreateUser");

        NewUserDto newUser = new NewUserDto("test@mail.com", "Test", "testuser", "12345", "12345");
        User user = new User();
        user.setId(1L);
        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());

        Role role = new Role();
        role.setName("ROLE_USER");

        when(userMapper.newUserRequestDtoToUser(newUser)).thenReturn(user);
        when(roleRepository.getByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        log.info("Попытка добавления пользователя: {}", newUser);
        User result = userService.add(newUser);
        log.info("Добавлен пользователь: {}", result);

        log.info("Проверка добавления пользователя и его данных");
        assertEquals(user, result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(any(User.class));

        log.info("Пользователь успешно добавлен, данные совпадают");
    }

    @Test
    @DisplayName("getById_ShouldReturnUserDto: должен вернуть пользователя по ID")
    void getById_ShouldReturnUserDto() {
        log.info("Запуск теста getById_ShouldReturnUserDto");

        User user = new User(1L, "test@mail.com", "Test");
        UserDto dto = new UserDto(1L, "test@mail.com", "Test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(dto);

        log.info("Поиск пользователя по ID: {}", dto.getId());
        UserDto result = userService.getById(1L);
        log.info("Найден пользователь: {}", result);

        log.info("Сравнение полученных данных {} с искомыми {}", dto, result);
        assertEquals(dto, result);
        assertEquals(dto.getEmail(), result.getEmail());

        log.info("Данные совпадают");
    }

    @Test
    @DisplayName("delete_ShouldCallRepositoryDelete: должен удалить пользователя по ID")
    void delete_ShouldCallRepositoryDelete() {
        log.info("Запуск теста delete_ShouldCallRepositoryDelete");

        log.info("Удаление пользователя с ID = 1");
        userService.delete(1L);
        log.info("Проверка удаления пользователя с ID = {}", 1L);
        verify(userRepository).deleteById(1L);

        log.info("Пользователь успешно удален");
    }

    @Test
    @DisplayName("delete_ShouldThrowWhenNotFound: должен выбросить исключение при удалении несуществующего пользователя")
    void delete_ShouldThrowWhenNotFound() {
        log.info("Запуск теста delete_ShouldThrowWhenNotFound");

        log.info("Проверка на выбрасывание исключения при удалении несуществующего пользователя");
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(userRepository).deleteById(1L);
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> userService.delete(1L));

        log.warn("Удаление завершилось исключением: {}", ex.getMessage());
    }

    @Test
    @DisplayName("loadUserByUsername_ShouldReturnUser: должен вернуть пользователя по username")
    void loadUserByUsername_ShouldReturnUser() {
        log.info("Запуск теста loadUserByUsername_ShouldReturnUser");

        User user = new User(1L, "test@mail.com", "Test");
        user.setUsername("testuser");
        log.info("Данные пользователя: {}", user);

        log.info("Проверка поиска пользователя по username = {}", user.getUsername());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.loadUserByUsername("testuser");
        log.info("Найден пользователь по username 'testuser': {}", result);

        log.info("Проверка на совпадение данных, ожидаемые: {}, фактические: {}", user, result);
        assertEquals("testuser", result.getUsername());
        assertEquals(user, result);

        log.info("Пользователь успешно найден, данные совпадают");
    }

    @Test
    @DisplayName("update_ShouldUpdateUser: должен обновить данные пользователя")
    void update_ShouldUpdateUser() {
        log.info("Запуск теста update_ShouldUpdateUser");

        UpdateUserDto dto = new UpdateUserDto();
        User user = new User(1L, "test@mail.com", "Test");
        log.info("Изначальные данные пользователя: {}", user);

        log.info("Обновление данных пользователя с ID = {}", user.getId());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.update(1L, dto);
        log.info("Данные пользователя после обновления: {}", result);

        log.info("Проверка обновленных данных");
        assertEquals(1L, result.getId());

        log.info("Данные успешно обновлены");
    }
}
