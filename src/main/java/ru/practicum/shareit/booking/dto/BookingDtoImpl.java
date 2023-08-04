package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoImpl {
    long bookingId;
    //@JsonProperty("item.id")
    long itemId;
    //@JsonProperty("item.name")
    String itemName;
    //@JsonProperty("booker.id")
    long bookerId;
    //@JsonProperty("status")
    BookingStatus bookingStatus;
    LocalDateTime start;
    LocalDateTime end;

}
