package ru.litu.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAuthException extends AuthenticationException {

    public UserAuthException(String message) {
        super(message);
    }
}
