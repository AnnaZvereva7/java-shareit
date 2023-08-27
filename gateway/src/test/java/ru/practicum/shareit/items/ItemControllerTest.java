package ru.practicum.shareit.items;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.ErrorHandler;
import ru.practicum.shareit.items.dto.CommentDtoRequest;
import ru.practicum.shareit.items.dto.ItemDto;

import java.nio.charset.StandardCharsets;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemClient client;
    @InjectMocks
    private ItemController controller;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void findById_whenOk() throws Exception {
        mvc.perform(get("/items/{itemId}", 1L)
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByUser_whenOk() throws Exception {
        mvc.perform(get("/items")
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void save_whenOk() throws Exception {
        ItemDto itemDto= new ItemDto(null, "name", "description", true, 1L);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void save_whenWrongName() throws Exception {
        ItemDto itemDto= new ItemDto(null, "  ", "description", true, 1L);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).save(anyLong(), any(ItemDto.class));
    }
    @Test
    void save_whenLongName() throws Exception {
        ItemDto itemDto= new ItemDto(null, "SomeNameItemSomeNameItemSomeNameItemSomeNameItemSomeNameItem", "description", true, 1L);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void save_whenWrongDescription() throws Exception {
        ItemDto itemDto= new ItemDto(null, "SomeNameItem", "   ", true, 1L);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void save_whenWrongAvailable() throws Exception {
        ItemDto itemDto= new ItemDto(null, "SomeNameItem", "description", null, 1L);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).save(anyLong(), any(ItemDto.class));
    }

    @Test
    void update_whenWrongName() throws Exception {
        ItemDto itemDto= new ItemDto(null, "SomeNameItemSomeNameItemSomeNameItemSomeNameItemSomeNameItem", "description", null, 1L);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void update_whenOk() throws Exception {
        ItemDto itemDto= new ItemDto(null, null, "description", null, 1L);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findByText() throws Exception {
        mvc.perform(get("/items/search")
                        .header(Constants.USERID, 1L)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        CommentDtoRequest commentDto = new CommentDtoRequest("text");
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_whenBlank() throws Exception {
        CommentDtoRequest commentDto = new CommentDtoRequest("   ");
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).addComment(anyLong(), anyLong(), any(CommentDtoRequest.class));
    }
    @Test
    void addComment_whenLong() throws Exception {
        CommentDtoRequest commentDto = new CommentDtoRequest("SomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeComment"+
                "SomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeComment"+
                "SomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeCommentSomeComment");
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(client, never()).addComment(anyLong(), anyLong(), any(CommentDtoRequest.class));
    }
}