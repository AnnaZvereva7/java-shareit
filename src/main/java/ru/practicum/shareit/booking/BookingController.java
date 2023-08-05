package ru.practicum.shareit.booking;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.LimitAccessException;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    Booking create(@RequestBody @Validated BookingDtoRequest bookingDto,
                   @RequestHeader(Constants.USERID) @NotNull long bookerId) {
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    Booking approval(@RequestHeader(Constants.USERID) @NotNull long userId,
                     @RequestParam @NotNull boolean approved,
                     @PathVariable @NotNull long bookingId) {
        if (bookingService.isUserOwner(userId, bookingId)) {
            return bookingService.approval(bookingId, approved);
        } else {
            throw new LimitAccessException(" approval");
        }
    }

    @GetMapping("/{bookingId}")
    Booking findById(@RequestHeader(Constants.USERID) @NotNull long userId,
                     @PathVariable @NotNull long bookingId) {
        if (bookingService.isUserBooker(userId, bookingId)
                || bookingService.isUserOwner(userId, bookingId)) {
            return bookingService.findById(bookingId);
        } else {
            throw new LimitAccessException("booking");
        }
    }

    @GetMapping
    List<Booking> findAllByBooker(@RequestHeader(Constants.USERID) @NotNull long userId,
                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    List<Booking> findAllByOwner(@RequestHeader(Constants.USERID) @NotNull long userId,
                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwner(userId, state);
    }

}
