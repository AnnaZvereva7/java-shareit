package ru.practicum.shareit.exception;

public class NotUniqueEmailException extends RuntimeException {
    public NotUniqueEmailException() {
        super("Email не уникален");
    }
}
