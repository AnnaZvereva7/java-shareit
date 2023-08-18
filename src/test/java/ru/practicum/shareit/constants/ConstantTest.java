package ru.practicum.shareit.constants;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ConstantTest {
    public static final LocalDateTime NOW = LocalDateTime.of(2023,8,10,12,0);
    public static final Clock CLOCK= Clock.fixed(Instant.parse("2023-08-10T12:00:00.00Z"), ZoneId.of("UTC"));
}
