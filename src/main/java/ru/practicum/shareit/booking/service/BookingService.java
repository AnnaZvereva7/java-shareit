package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingShortDto bookingDto, long bookerId);

    Booking approval(long bookingId, boolean isApproved);

    boolean isUserOwner(long userId, long bookingId);

    boolean isUserBooker(long userId, long bookingId);

    Booking findById(long bookingId);

    List<Booking> findAllByOwner(long ownerId, String state);

    List<Booking> findAllByBooker(long bookerId, String state);

    boolean checkForComment(long userId, long itemId);
}
