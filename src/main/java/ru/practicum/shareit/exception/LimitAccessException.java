package ru.practicum.shareit.exception;

public class LimitAccessException extends RuntimeException {
    public LimitAccessException() {
        super("Доступ для внесения изменений запрещен");
    }
}
