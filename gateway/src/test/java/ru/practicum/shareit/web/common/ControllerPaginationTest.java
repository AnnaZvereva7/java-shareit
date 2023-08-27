package ru.practicum.shareit.web.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.common.Constants;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureWebClient
public class ControllerPaginationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findAllBookingByBooker_whenWrongFrom() throws Exception {
        mvc.perform(get("/bookings")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "-4")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void findAllBookingByBooker_whenWrongSize() throws Exception {
        mvc.perform(get("/bookings")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllBookingByOwner_whenWrongSize() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void findAllBookingByOwner_whenWrongFrom() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(Constants.USERID, 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllItemsByUser_whenWrongFrom() throws Exception {
        mvc.perform(get("/items")
                        .header(Constants.USERID, 1L)
                        .param("from", "-1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllItemsByUser_whenWrongSize() throws Exception {
        mvc.perform(get("/items")
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "-2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void findItemsByText_whenWrongFrom() throws Exception {
        mvc.perform(get("/items/search", 1L)
                        .header(Constants.USERID, 1L)
                        .param("text", "text")
                        .param("from", "-2")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findItemsByText_whenWrongSize() throws Exception {
        mvc.perform(get("/items/search", 1L)
                        .header(Constants.USERID, 1L)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void findAllRequests_whenWrongFrom() throws Exception{
        mvc.perform(get("/requests/all")
                        .header(Constants.USERID, 1L)
                        .param("from", "-1")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void findAllRequests_whenWrongSize() throws Exception{
        mvc.perform(get("/requests/all")
                        .header(Constants.USERID, 1L)
                        .param("from", "1")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
