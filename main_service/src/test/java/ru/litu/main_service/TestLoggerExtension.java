package ru.litu.main_service;

import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLoggerExtension implements TestWatcher, BeforeAllCallback, AfterAllCallback {
    private static final Logger log = LoggerFactory.getLogger(TestLoggerExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println("------- Начало тестирования -------");
    }

    @Override
    public void afterAll(ExtensionContext context) {
        System.out.println("------- Все тесты успешно пройдены -------");
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("Тест {} успешно пройден", context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("Тест {} завершился с ошибкой: {}", context.getDisplayName(), cause.getMessage());
    }
}
