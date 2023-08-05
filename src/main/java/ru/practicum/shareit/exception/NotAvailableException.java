package ru.practicum.shareit.exception;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException(String message) {
        super("not available "+message);
    }
}
