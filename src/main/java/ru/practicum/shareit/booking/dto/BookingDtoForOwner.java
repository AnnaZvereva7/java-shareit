package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface BookingDtoForOwner {
    long getId();

    long getItemId();

    long getBookerId();

    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
}
