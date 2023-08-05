package ru.practicum.shareit.exception;

public class LimitAccessException extends RuntimeException {
    public LimitAccessException(String message) {
        super("Limit access to "+ message);
    }
}
