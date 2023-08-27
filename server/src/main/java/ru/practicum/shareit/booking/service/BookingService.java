package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDtoRequest bookingDto, long bookerId);

    Booking approval(long bookingId, boolean isApproved);

    boolean isUserOwner(long userId, long bookingId);

    boolean isUserBooker(long userId, long bookingId);

    Booking findById(long bookingId);

    List<Booking> findAllByOwner(long ownerId, State state, int from, int size);

    List<Booking> findAllByBooker(long bookerId, State state, int from, int size);

    boolean checkForComment(long userId, long itemId);

}
