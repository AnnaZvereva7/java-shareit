package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.State;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).setControllerAdvice(ErrorHandler.class).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @Test
    void create_whenOk() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        User booker = new User(1L, "userName", "email@mail.ru");
        Item item = new Item(1L, "itemName", "description", true, 2L, null);
        BookingDtoRequest newBooking = new BookingDtoRequest(1L, now.plusDays(1), now.plusDays(2));
        when(service.create(any(BookingDtoRequest.class), anyLong())).thenReturn(new Booking(1L, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.WAITING));
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(now.plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("itemName")))
                .andExpect(jsonPath("$.item.description", is("description")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.ownerId", is(2L), Long.class))
                .andExpect(jsonPath("$.item.requestId", is(nullValue())))

                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.booker.name", is("userName")))
                .andExpect(jsonPath("$.booker.email", is("email@mail.ru")));

    }

    @Test
    void create_whenInPast() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        BookingDtoRequest newBooking = new BookingDtoRequest(1L, now.minusDays(1), now.plusDays(2));
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenEndBeforeStart() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        BookingDtoRequest newBooking = new BookingDtoRequest(1L, now.plusDays(4), now.plusDays(2));
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void approval_whenOk() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        User booker = new User(1L, "userName", "email@mail.ru");
        Item item = new Item(1L, "itemName", "description", true, 2L, null);
        when(service.isUserOwner(1L, 1L)).thenReturn(true);
        when(service.approval(1L, true)).thenReturn(new Booking(1L, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.APPROVED));
        //when
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .param("approved", "TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(now.plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("itemName")))
                .andExpect(jsonPath("$.item.description", is("description")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.ownerId", is(2L), Long.class))
                .andExpect(jsonPath("$.item.requestId", is(nullValue())))

                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.booker.name", is("userName")))
                .andExpect(jsonPath("$.booker.email", is("email@mail.ru")));

    }

    @Test
    void approval_whenLimitAccess() throws Exception {
        //given
        when(service.isUserOwner(1L, 1L)).thenReturn(false);
        //when
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .param("approved", "TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Limit access to approval")));
    }

    @Test
    void findById_whenOk() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        User booker = new User(1L, "userName", "email@mail.ru");
        Item item = new Item(1L, "itemName", "description", true, 2L, null);
        when(service.isUserOwner(1L, 1L)).thenReturn(true);
        when(service.isUserBooker(1L, 1L)).thenReturn(false);
        when(service.findById(1L)).thenReturn(new Booking(1L, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.APPROVED));
        //when
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", is(now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(now.plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("itemName")))
                .andExpect(jsonPath("$.item.description", is("description")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.ownerId", is(2L), Long.class))
                .andExpect(jsonPath("$.item.requestId", is(nullValue())))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.booker.name", is("userName")))
                .andExpect(jsonPath("$.booker.email", is("email@mail.ru")));
    }

    @Test
    void findById_whenLimitAccess() throws Exception {
        //given
        when(service.isUserOwner(1L, 1L)).thenReturn(false);
        when(service.isUserBooker(1L, 1L)).thenReturn(false);
        //when
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Limit access to booking")));
    }

    @Test
    void findAllByBooker_whenOk() throws Exception {
        //given
        when(service.findAllByBooker(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(List.of(new Booking()));
        //when
        mvc.perform(get("/bookings")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(nullValue())))
                .andExpect(jsonPath("$[0].start", is(nullValue())))
                .andExpect(jsonPath("$[0].end", is(nullValue())))
                .andExpect(jsonPath("$[0].status", is(nullValue())))
                .andExpect(jsonPath("$[0].item", is(nullValue())))
                .andExpect(jsonPath("$[0].booker", is(nullValue())));
    }

    @Test
    void findAllByBooker_whenWrongState() throws Exception {
        //when
        mvc.perform(get("/bookings")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALLA")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: ALLA")));
    }

    @Test
    void findAllByOwner_whenOk() throws Exception {
        //given
        when(service.findAllByOwner(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(List.of(new Booking()));
        //when
        mvc.perform(get("/bookings/owner")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(nullValue())))
                .andExpect(jsonPath("$[0].start", is(nullValue())))
                .andExpect(jsonPath("$[0].end", is(nullValue())))
                .andExpect(jsonPath("$[0].status", is(nullValue())))
                .andExpect(jsonPath("$[0].item", is(nullValue())))
                .andExpect(jsonPath("$[0].booker", is(nullValue())));
    }

    @Test
    void findAllByOwner_whenStatusUnknown() throws Exception {
        //given
        String state = "UNSUPPORTED_STATUS";
        //when
        mvc.perform(get("/bookings/owner")
                        .header(Constants.USERID, 1L)
                        .param("state", state))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));
    }

}