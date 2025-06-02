package ru.litu.main_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.litu.main_service.security.JwtTokenProvider;
import ru.litu.main_service.user.mapper.UserMapper;
import ru.litu.main_service.user.service.UserService;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public UserMapper userMapper() {
        return mock(UserMapper.class);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }
}
