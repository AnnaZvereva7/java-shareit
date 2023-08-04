package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface BookingPeriod {
    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
}
