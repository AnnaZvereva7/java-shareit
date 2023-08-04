package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public interface BookingDto {
    @JsonProperty("id")
    Long getBookingId();

    @JsonProperty("item.id")
    Long getItemId();

    @JsonProperty("item.name")
    String getItemName();

    @JsonProperty("booker.id")
    Long getBookerId();

    @JsonProperty("status")
    BookingStatus getBookingStatus();

    LocalDateTime getStart();

    LocalDateTime getEnd();
}
