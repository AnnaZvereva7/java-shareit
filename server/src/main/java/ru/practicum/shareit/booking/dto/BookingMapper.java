package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.users.dto.UserDto;

@Component
public class BookingMapper {
    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                new UserDto(booking.getBooker().getId(),
                        booking.getBooker().getName(),
                        booking.getBooker().getEmail()),
                new ItemDto(booking.getItem().getId(),
                        booking.getItem().getName(),
                        booking.getItem().getDescription(),
                        booking.getItem().getAvailable(),
                        booking.getItem().getRequestId()));
    }
}
