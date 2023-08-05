package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long bookingId;
    private long itemId;
    private String itemName;
    private long bookerId;
    private BookingStatus bookingStatus;
    private LocalDateTime start;
    private LocalDateTime end;

}
