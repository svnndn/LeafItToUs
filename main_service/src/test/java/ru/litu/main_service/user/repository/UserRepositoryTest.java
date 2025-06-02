package ru.litu.main_service.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.litu.main_service.TestLoggerExtension;
import ru.litu.main_service.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(TestLoggerExtension.class)
class UserRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmail: должен вернуть пользователя по email")
    void findByEmail_ShouldReturnUser() {
        log.info("Запуск теста findByEmail_ShouldReturnUser");
        User user = new User();
        user.setEmail("test@mail.com");
        user.setUsername("testuser");
        user.setName("Test");
        user.setPassword("password");

        log.info("Создание пользователя: {}", user);
        userRepository.save(user);

        log.info("Поиск пользователя по email: {}", user.getEmail());
        Optional<User> result = userRepository.findByEmail("test@mail.com");
        log.info("Найден пользователь: {}", result);

        log.info("Проверка полученных данных");
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());

        log.info("Пользователь успешно найден по email, данные совпадают");
    }

    @Test
    @DisplayName("findByUsername: должен вернуть пользователя по username")
    void findByUsername_ShouldReturnUser() {
        log.info("Запуск теста findByUsername_ShouldReturnUser");
        User user = new User();
        user.setEmail("user@mail.com");
        user.setUsername("uniqueuser");
        user.setName("UserName");
        user.setPassword("password");

        log.info("Создание пользователя: {}", user);
        userRepository.save(user);

        log.info("Поиск пользователя по username: {}", user.getUsername());
        Optional<User> result = userRepository.findByUsername("uniqueuser");
        log.info("Найден пользователь: {}", result);

        log.info("Проверка полученных данных");
        assertTrue(result.isPresent());
        assertEquals("user@mail.com", result.get().getEmail());

        log.info("Пользователь успешно найден по username, данные совпадают");
    }

    @Test
    @DisplayName("findAllByIdIn: должен вернуть список пользователей по списку ID")
    void findAllByIdIn_ShouldReturnUsers() {
        log.info("Запуск теста findAllByIdIn_ShouldReturnUsers");

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setUsername("user1");
        user1.setName("User 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setUsername("user2");
        user2.setName("User 2");
        user2.setPassword("password");

        log.info("Сохраняем пользователей: {} и {}", user1.getUsername(), user2.getUsername());
        userRepository.saveAll(List.of(user1, user2));

        List<Long> ids = userRepository.findAll().stream().map(User::getId).toList();
        log.info("Получены ID пользователей: {}", ids);

        Page<User> result = userRepository.findAllByIdIn(ids, PageRequest.of(0, 10));
        log.info("Найдено пользователей по ID: {}", result.getTotalElements());
        log.info("Пользователи: {}", result.getContent().stream().map(User::getUsername).toList());

        log.info("Проверка результатов");
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).extracting(User::getUsername).containsExactlyInAnyOrder("user1", "user2");

        log.info("Тест findAllByIdIn успешно пройден");
    }

    @Test
    @DisplayName("findByEmail: должен вернуть пустой Optional, если email не найден")
    void findByEmail_ShouldReturnEmpty() {
        log.info("Запуск теста findByEmail_ShouldReturnEmpty");
        Optional<User> result = userRepository.findByEmail("nonexistent@mail.com");
        log.info("Результат поиска по несуществующему email: {}", result);

        log.info("Проверка, что результат пустой");
        assertTrue(result.isEmpty());

        log.info("Тест findByEmail с пустым результатом успешно пройден");
    }

    @Test
    @DisplayName("findByUsername: должен вернуть пустой Optional, если username не найден")
    void findByUsername_ShouldReturnEmpty() {
        log.info("Запуск теста findByUsername_ShouldReturnEmpty");
        Optional<User> result = userRepository.findByUsername("nouser");
        log.info("Результат поиска по несуществующему username: {}", result);

        log.info("Проверка, что результат пустой");
        assertTrue(result.isEmpty());

        log.info("Тест findByUsername с пустым результатом успешно пройден");
    }
}