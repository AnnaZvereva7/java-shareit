package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public BookingDtoImpl toBookingDto(Booking booking) {
        return new BookingDtoImpl(booking.getId(),
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getBooker().getId(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd());
    }
}
