package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.LimitAccessException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    BookingDtoResponse create(@RequestBody BookingDtoRequest bookingDto,
                              @RequestHeader(Constants.USERID) Long bookerId) {
        return mapper.toBookingDtoResponse(bookingService.create(bookingDto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    BookingDtoResponse approval(@RequestHeader(Constants.USERID) Long userId,
                                @RequestParam Boolean approved,
                                @PathVariable Long bookingId) {
        if (bookingService.isUserOwner(userId, bookingId)) {
            return mapper.toBookingDtoResponse(bookingService.approval(bookingId, approved));
        } else {
            throw new LimitAccessException("approval");
        }
    }

    @GetMapping("/{bookingId}")
    BookingDtoResponse findById(@RequestHeader(Constants.USERID) Long userId,
                                @PathVariable Long bookingId) {
        if (bookingService.isUserBooker(userId, bookingId)
                || bookingService.isUserOwner(userId, bookingId)) {
            return mapper.toBookingDtoResponse(bookingService.findById(bookingId));
        } else {
            throw new LimitAccessException("booking");
        }
    }

    @GetMapping
        //pagination
    List<BookingDtoResponse> findAllByBooker(@RequestHeader(Constants.USERID) Long userId,
                                             @RequestParam String state,
                                             @RequestParam int from,
                                             @RequestParam int size) {

        State stateEnum = State.valueOf(state);
        return bookingService.findAllByBooker(userId, stateEnum, from, size)
                .stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
        //pagination
    List<BookingDtoResponse> findAllByOwner(@RequestHeader(Constants.USERID) Long userId,
                                            @RequestParam String state,
                                            @RequestParam int from,
                                            @RequestParam int size) {
        State stateEnum = State.valueOf(state);
        return bookingService.findAllByOwner(userId, stateEnum, from, size)
                .stream()
                .map(mapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
