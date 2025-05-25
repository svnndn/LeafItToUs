package ru.litu.main_service.exception;

public class SQLConstraintViolationException extends RuntimeException {
    public SQLConstraintViolationException(String message) {
        super(message);
    }
}