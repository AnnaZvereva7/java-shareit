package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository repository,
                              @Qualifier("DBItem") ItemRepository itemRepository,
                              @Qualifier("DBUsers") UserRepository userRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking create(BookingShortDto bookingDto, long bookerId) {
        if (isBookingAvailable(bookingDto, bookerId)) {
            Booking booking = new Booking(null,
                    bookingDto.getStart(),
                    bookingDto.getEnd(),
                    itemRepository.findById(bookingDto.getItemId()),
                    userRepository.findById(bookerId),
                    BookingStatus.WAITING);
            return repository.save(booking);
        } else {
            throw new NotAvailableException();
        }
    }

    private boolean isBookingAvailable(BookingShortDto bookingDto, long bookerId) {
        long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId);
        if (item.getOwnerId() == bookerId) {
            throw new BookingByOwnerException("Нельзя забронировать свою вещь");
        } else if (!item.getAvailable()) {
            return false;
        } else {
            LocalDateTime start = bookingDto.getStart();
            LocalDateTime end = bookingDto.getEnd();
            List<BookingPeriod> periods = repository.findAllBookingPeriodsForItemId(itemId, LocalDateTime.now());
            if (periods.isEmpty()) {
                return true;
            }
            if (periods.get(0).getStart().isAfter(end)) {
                return true;
            } else if (periods.get(periods.size() - 1).getEnd().isBefore(start)) {
                return true;
            } else if (periods.size() > 1) {
                for (int i = 0; i < periods.size() - 1; i++) {
                    if (periods.get(i).getEnd().isBefore(start) && periods.get(i + 1).getStart().isAfter(end)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Booking approval(long bookingId, boolean isApproved) {
        isStatusCorrect(bookingId);
        if (isApproved) {
            repository.changeStatus(bookingId, BookingStatus.APPROVED.toString());
        } else {
            repository.changeStatus(bookingId, BookingStatus.REJECTED.toString());
        }
        return repository.findById(bookingId);
    }

    private boolean isStatusCorrect(long bookingId) {
        String status = repository.getStatusById(bookingId);
        if (status == null) {
            throw new NotFoundException("бронирование не найдено");
        } else if (status.equals("WAITING")) {
            return true;
        } else {
            throw new WrongStatusException("Статус " + status + " не может быть изменен");
        }
    }

    @Override
    public boolean isUserOwner(long userId, long bookingId) {
        return repository.countByOwnerIdBookingId(userId, bookingId) == 1;
    }

    @Override
    public boolean isUserBooker(long userId, long bookingId) {
        return repository.countByBookerIdBookingId(userId, bookingId) == 1;
    }

    @Override
    public Booking findById(long bookingId) {
        return repository.findById(bookingId);
    }

    @Override
    public List<Booking> findAllByOwner(long ownerId, String state) {
        userRepository.findById(ownerId);
        List<Booking> bookings = repository.findAllByOwnerId(ownerId);
        return filterByState(bookings, state);
    }

    @Override
    public List<Booking> findAllByBooker(long bookerId, String state) {
        userRepository.findById(bookerId);
        List<Booking> bookings = repository.findAllByBookerIdOrderByStartDesc(bookerId);
        return filterByState(bookings, state);
    }

    private List<Booking> filterByState(List<Booking> bookings, String state) {
        switch (state) {
            case "ALL":
                return bookings;
            case "PAST":
                return bookings
                        .stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings
                        .stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings
                        .stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings
                        .stream()
                        .filter(booking -> (booking.getStatus() == BookingStatus.APPROVED
                                || booking.getStatus() == BookingStatus.REJECTED
                                || booking.getStatus() == BookingStatus.WAITING)
                                && booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings
                        .stream()
                        .filter(booking -> (booking.getStatus() == BookingStatus.APPROVED
                                || booking.getStatus() == BookingStatus.WAITING
                                || booking.getStatus() == BookingStatus.REJECTED)
                                && booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
    }

    public boolean checkForComment(long userId, long itemId) {
        if (repository.checkForComment(userId, itemId, LocalDateTime.now()) >= 1) {
            return true;
        } else {
            throw new NotFoundCommentException("нет бронирования для комментария");
        }
    }
}
