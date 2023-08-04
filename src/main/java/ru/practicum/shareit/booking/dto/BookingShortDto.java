package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingShortDto {
    @NotNull
    Long itemId;
    @NotNull
    @Future
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;

    public void checkPeriod() {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new DateTimeException("начало периода позже окончания периода");
        }
    }
}
