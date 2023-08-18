package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.users.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingRepositoryImpl repositoryImpl;
    private final UserService userService;
    private final ItemService itemService;

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
        return repository.findById(bookingId).get();
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
        Optional<Booking> booking=repository.findById(bookingId);
        if(booking.isEmpty()) {
            throw new NotFoundException(Booking.class);
        } else {
         return booking.get();
        }
    }

    @Override
    public List<Booking> findAllByOwner(long ownerId, State state, int from, int size) {
        userService.findById(ownerId);
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by("startDate").descending());
        switch (state) {
            case ALL:
                return repositoryImpl.findAllByOwnerId(ownerId, from, size);
            case PAST:
                return repositoryImpl.findPastByOwnerId(ownerId, LocalDateTime.now(), from, size);
            case WAITING:
                return repositoryImpl.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, from, size);
            case REJECTED:
                return repositoryImpl.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, from, size);
            case CURRENT:
                return repositoryImpl.findCurrentByOwnerId(ownerId, LocalDateTime.now(), from, size);
            case FUTURE:
                return repositoryImpl.findFutureByOwnerId(ownerId, LocalDateTime.now(), from, size);
            default:
                throw new RuntimeException("unknown state");
        }
    }

    @Override
    @Transactional
    public List<Booking> findAllByBooker(long bookerId, State state, int from, int size) {
        userService.findById(bookerId);
        switch (state) {
            case ALL:
                return repositoryImpl.findAllByBookerId(bookerId, from, size);
            case WAITING:
                return repositoryImpl.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, from, size);
            case REJECTED:
                return repositoryImpl.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, from, size);
            case FUTURE:
                return repositoryImpl.findAllByBookerIdAndStartDateAfter(bookerId, LocalDateTime.now(), from, size);
            case PAST:
                return repositoryImpl.findAllByBookerIdAndEndDateBefore(bookerId, LocalDateTime.now(), from, size);
            case CURRENT:
                return repositoryImpl.findAllByBookerIdAndEndDateAfterAndStartDateBefore(bookerId, LocalDateTime.now(), from, size);
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
