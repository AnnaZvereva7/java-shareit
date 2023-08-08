package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;


    public BookingServiceImpl(BookingRepository repository,
                              UserService userService, ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public Booking create(BookingDtoRequest bookingDto, long bookerId) {
        if (isBookingAvailable(bookingDto, bookerId)) {
            Booking booking = new Booking(null,
                    bookingDto.getStart(),
                    bookingDto.getEnd(),
                    itemService.findById(bookingDto.getItemId()),
                    userService.findById(bookerId),
                    BookingStatus.WAITING);
            return repository.save(booking);
        } else {
            throw new NotAvailableException("for booking");
        }
    }

    private boolean isBookingAvailable(BookingDtoRequest bookingDto, long bookerId) {
        long itemId = bookingDto.getItemId();
        Item item = itemService.findById(itemId);
        if (item.getOwnerId() == bookerId) {
            throw new LimitAccessException("Нельзя забронировать свою вещь");
        } else if (!item.getAvailable()) {
            return false;
        } else {
            LocalDateTime start = bookingDto.getStart();
            LocalDateTime end = bookingDto.getEnd();
            List<BookingPeriod> periods = repository.findAllBookingPeriodsForItemId(itemId, LocalDateTime.now());
            if (periods.isEmpty()) {
                return true;
            }
            if (periods.get(0).getStartDate().isAfter(end)) {
                return true;
            } else if (periods.get(periods.size() - 1).getEndDate().isBefore(start)) {
                return true;
            } else if (periods.size() > 1) {
                for (int i = 0; i < periods.size() - 1; i++) {
                    if (periods.get(i).getEndDate().isBefore(start) && periods.get(i + 1).getStartDate().isAfter(end)) {
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
            throw new NotFoundException(Booking.class);
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
    public List<Booking> findAllByOwner(long ownerId, State state) {
        userService.findById(ownerId);
        switch (state) {
            case ALL:
                return repository.findAllByOwnerId(ownerId);
            case PAST:
                return repository.findPastByOwnerId(ownerId, LocalDateTime.now());
            case WAITING:
                return repository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING.name());
            case REJECTED:
                return repository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED.name());
            case CURRENT:
                return repository.findCurrentByOwnerId(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE:
                return repository.findFutureByOwnerId(ownerId, LocalDateTime.now());
            default:
                throw new RuntimeException("unknown state");
        }
    }

    @Override
    public List<Booking> findAllByBooker(long bookerId, State state) {
        userService.findById(bookerId);
        switch (state) {
            case ALL:
                return repository.findAllByBookerIdOrderByStartDateDesc(bookerId);
            case WAITING:
                return repository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.WAITING);
            case REJECTED:
                return repository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId, BookingStatus.REJECTED);
            case FUTURE:
                return repository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, LocalDateTime.now());
            case PAST:
                return repository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId, LocalDateTime.now());
            case CURRENT:
                return repository.findAllByBookerIdAndEndDateAfterAndStartDateBeforeOrderByStartDateDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
            default:
                throw new RuntimeException("unknown state");
        }
    }


    public boolean checkForComment(long userId, long itemId) {
        if (repository.checkForComment(userId, itemId, LocalDateTime.now()) >= 1) {
            return true;
        } else {
            throw new NotAvailableException("for comment");
        }
    }

}
