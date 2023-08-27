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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.ErrorHandler;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingClient client;
    @InjectMocks
    private BookingController controller;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllByBooker_whenWrongState() throws Exception {
        mvc.perform(get("/bookings")
                        .header(Constants.USERID, 1L)
                        .param("state", "string")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: string")));
    }

    @Test
    void findAllByBooker_whenOk() throws Exception {
        mvc.perform(get("/bookings")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void create_whenWrongDtoStartBeforeNow() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        BookingDto newBooking = new BookingDto(1L, now.minusDays(1), now.plusDays(2));
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
    void create_whenWrongDtoStartBeforeEnd() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        BookingDto newBooking = new BookingDto(1L, now.plusDays(4), now.plusDays(2));
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
    void create_whenOk() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        BookingDto newBooking = new BookingDto(1L, now.plusDays(1), now.plusDays(2));
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBooking))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
//        ResponseEntity<Object> responseEntity = new ResponseEntity<>(new Object(), HttpStatus.OK);
//        when(client.findById(1L, 1L)).thenReturn(responseEntity);
        //when
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Test
    void approval_whenOk() throws Exception {
        //when
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Test
    void approval_whenWrongParam() throws Exception {
        //when
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Constants.USERID, 1L)
                        .param("approved", "approved")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllByOwner_whenWrongState() throws Exception {
        //when
        mvc.perform(get("/bookings/owner")
                        .header(Constants.USERID, 1L)
                        .param("state", "string")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: string")));
    }

    @Test
    void findAllByOwner_whenOk() throws Exception {
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
                .andExpect(status().isOk());
    }

}