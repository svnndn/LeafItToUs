package ru.litu.main_service.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAuthException extends AuthenticationException {

    public UserAuthException(String message) {
        super(message);
    }
}
