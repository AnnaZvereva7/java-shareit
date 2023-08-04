package ru.practicum.shareit.exception;

public class BookingByOwnerException extends RuntimeException {
    public BookingByOwnerException(String message) {
        super(message);
    }
}
