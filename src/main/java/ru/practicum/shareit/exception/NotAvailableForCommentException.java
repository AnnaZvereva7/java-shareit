package ru.practicum.shareit.exception;

public class NotAvailableForCommentException extends RuntimeException {
    public NotAvailableForCommentException(String message) {
        super("Нет возможности оставить отзыв");
    }
}
