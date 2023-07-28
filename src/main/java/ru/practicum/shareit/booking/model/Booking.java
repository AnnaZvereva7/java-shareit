package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private final int id;
    private final LocalDate start;
    private final LocalDate end;
    private final int itemId;
    private final int bookerId;
    private final BookingStatus status;

}
