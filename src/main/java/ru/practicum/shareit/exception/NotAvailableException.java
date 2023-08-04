package ru.practicum.shareit.exception;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException() {
        super("не доступно для бронирования");
    }
}
