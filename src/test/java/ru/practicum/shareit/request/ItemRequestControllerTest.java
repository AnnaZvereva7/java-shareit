package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImp;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService service;
    @Mock
    private ItemServiceImp itemService;
    @InjectMocks
    private ItemRequestController controller;

    private Clock clock;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2023-08-10T12:00:00.00Z"),
                ZoneId.of("UTC"));
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void save_whenOk() throws Exception {
        //given
        ItemRequestDto itemRequestDto = new ItemRequestDto("description");
        User user = new User(1L, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(null, "description", user, LocalDateTime.now(clock));
        ItemRequest expectedItemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(clock));
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description", LocalDateTime.now(clock), List.of());
        when(userService.findById(1L)).thenReturn(user);
        when(mapper.fromDto(itemRequestDto, user)).thenReturn(itemRequest);
        when(service.save(itemRequest)).thenReturn(expectedItemRequest);
        when(mapper.toDtoResponse(expectedItemRequest)).thenReturn(itemRequestDtoResponse);
        //when
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is(LocalDateTime.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items.size()", is(0)));
    }

    @Test
    void save_whenWrongUser() throws Exception {
        //given
        ItemRequestDto itemRequestDto = new ItemRequestDto("description");
        when(userService.findById(1L)).thenThrow(new NotFoundException(User.class));

        //when
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.users.model.User not found")));
    }

    @Test
    void save_whenEmptyDescription() throws Exception {
        //given
        ItemRequestDto itemRequestDto = new ItemRequestDto("   ");
        //when
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByUser_whenOk() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "requestDescription",
                LocalDateTime.now(clock), List.of(itemDto));
        when(userService.findById(1L)).thenReturn(new User());
        when(service.findByUser(1L)).thenReturn(List.of(itemRequestDtoResponse));
        //when
        mvc.perform(get("/requests")
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("requestDescription")))
                .andExpect(jsonPath("$[0].created", is(LocalDateTime.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items.size()", is(1)));
    }

    @Test
    void findByUser_whenWrongUser() throws Exception {
        //given
        when(userService.findById(1L)).thenThrow(new NotFoundException(User.class));
        //when
        mvc.perform(get("/requests")
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.users.model.User not found")));
    }

    @Test
    void findAllRequests_whenOk() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "requestDescription",
                LocalDateTime.now(clock), List.of(itemDto));
        when(userService.findById(1L)).thenReturn(new User());
        when(service.findByRequestorIdNot(1L, 0, 20)).thenReturn(List.of(itemRequestDtoResponse));
        //when
        mvc.perform(get("/requests/all")
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("requestDescription")))
                .andExpect(jsonPath("$[0].created", is(LocalDateTime.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items.size()", is(1)));
    }

    @Test
    void findById() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
        List<ItemDto> items = List.of(itemDto);
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "requestDescription",
                LocalDateTime.now(clock), items);
        ItemRequest itemRequest = new ItemRequest(1L, "requestDescription", null, LocalDateTime.now(clock));
        when(userService.findById(1L)).thenReturn(new User());
        when(itemService.findByRequestId(1L)).thenReturn(items);
        when(service.findById(1L)).thenReturn(itemRequest);
        when(mapper.toDtoResponseWithItems(itemRequest, items)).thenReturn(itemRequestDtoResponse);
        //when
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("requestDescription")))
                .andExpect(jsonPath("$.created", is(LocalDateTime.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items.size()", is(1)))
                .andExpect(jsonPath("$.items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$.items[0].name", is("name")))
                .andExpect(jsonPath("$.items[0].description", is("description")))
                .andExpect(jsonPath("$.items[0].available", is(true)))
                .andExpect(jsonPath("$.items[0].requestId", is(1L), Long.class));
    }

    @Test
    void findById_whenWrongRequestId() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
        List<ItemDto> items = List.of(itemDto);
        when(userService.findById(1L)).thenReturn(new User());
        when(itemService.findByRequestId(1L)).thenReturn(items);
        when(service.findById(1L)).thenThrow(new NotFoundException(ItemRequest.class));
        //when
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.request.ItemRequest not found")));
    }
}