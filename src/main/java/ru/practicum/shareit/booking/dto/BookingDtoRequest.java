package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validator.StartBeforeEndValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndValid
public class BookingDtoRequest {
    @NotNull
    private Long itemId;
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;

}
