package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.validator.StartBeforeEndValid;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndValid
public class BookingDto {
    private long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}
