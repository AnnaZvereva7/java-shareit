package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Validated
public class BookingJsonTests {
    @Autowired
    private JacksonTester<BookingDtoRequest> jsonDtoRequest;

    @Autowired
    private JacksonTester<Booking> jsonBooking;


    @Test
    public void testDeserialize() throws IOException {
        String jsonContent = "{\"itemId\":1, \"start\":\"2023-08-07T12:23:05\"," +
                " \"end\": \"2023-08-08T12:23:51\"}";

        BookingDtoRequest result = this.jsonDtoRequest.parseObject(jsonContent);

        assertThat(result.getItemId()).isEqualTo(1);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 8, 7, 12, 23, 5));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 8, 8, 12, 23, 51));
    }

    @Test
    public void testSerialize() throws Exception {

        Booking booking = new Booking(1L, LocalDateTime.of(2023, 8,10,12,0),
                LocalDateTime.of(2023,8,12,12,0), new Item(), new User(), BookingStatus.WAITING);

        JsonContent<Booking> result = this.jsonBooking.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("10-08-2023 12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("12-08-2023 12:00:00");

        assertThat(result).extractingJsonPathStringValue("$.item.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.available").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.ownerId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.requestId").isEqualTo(null);

        assertThat(result).extractingJsonPathStringValue("$.booker.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(null);

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
