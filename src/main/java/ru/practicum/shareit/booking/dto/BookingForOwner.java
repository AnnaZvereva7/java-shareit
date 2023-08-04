package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface BookingForOwner {
    long getId();

    long getBookerId();

    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
}
