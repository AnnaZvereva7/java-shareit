package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Validated
public class BookingJsonTests {
    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    public void testDeserialize() throws IOException {
        String jsonContent = "{\"itemId\":1, \"start\":\"2023-08-07T12:23:05\"," +
                " \"end\": \"2023-08-08T12:23:51\"}";

        BookingDtoRequest result = this.json.parseObject(jsonContent);

        assertThat(result.getItemId()).isEqualTo(1);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 8, 7, 12, 23, 5));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 8, 8, 12, 23, 51));
    }
}
