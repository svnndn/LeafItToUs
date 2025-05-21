package ru.litu.exception;

public class SQLConstraintViolationException extends RuntimeException {
    public SQLConstraintViolationException(String message) {
        super(message);
    }
}