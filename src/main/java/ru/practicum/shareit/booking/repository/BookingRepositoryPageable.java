package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepositoryPageable {
    List<Booking> findAllByBookerId(long bookerId, int from, int size);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, int from, int size);

    List<Booking> findAllByBookerIdAndStartDateAfter(long bookerId, LocalDateTime now, int from, int size);

    List<Booking> findAllByBookerIdAndEndDateBefore(long bookerId, LocalDateTime now, int from, int size);

    List<Booking> findAllByBookerIdAndEndDateAfterAndStartDateBefore(long bookerId, LocalDateTime now, int from, int size);

    List<Booking> findAllByOwnerId(long ownerId, int from, int size);

    List<Booking> findPastByOwnerId(long ownerId, LocalDateTime now, int from, int size);

    List<Booking> findByOwnerIdAndStatus(long ownerId, BookingStatus status, int from, int size);

    List<Booking> findCurrentByOwnerId(long ownerId, LocalDateTime now, int from, int size);

    List<Booking> findFutureByOwnerId(long ownerId, LocalDateTime now, int from, int size);

    List<BookingDtoForOwner> findLastBookingForItem(List<Long> itemsId, LocalDateTime now);

    List<BookingDtoForOwner> findNextBookingForItems(List<Long> itemsId, LocalDateTime now);
}
