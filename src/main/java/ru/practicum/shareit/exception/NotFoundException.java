package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Class<? extends Object> cl) {
        super("Entity " + cl + " not found");
    }
}
