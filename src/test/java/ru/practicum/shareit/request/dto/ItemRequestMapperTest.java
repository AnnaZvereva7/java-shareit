package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.users.model.User;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {
    private Clock clock;
    private ItemRequestMapper mapper;

    @BeforeEach
    public void before() {
        clock = Clock.fixed(
                Instant.parse("2023-08-10T12:00:00.00Z"),
                ZoneId.of("UTC"));
        mapper = new ItemRequestMapper(clock);
    }

    @Test
    void fromDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("description");
        User user = new User(1L, "name", "email@mail.ru");
        ItemRequest itemRequest = mapper.fromDto(itemRequestDto, user);

        assertEquals(null, itemRequest.getId());
        assertEquals("description", itemRequest.getDescription());
        assertEquals(LocalDateTime.now(clock), itemRequest.getCreated());
        assertEquals(1L, itemRequest.getRequestor().getId());
        assertEquals("name", itemRequest.getRequestor().getName());
        assertEquals("email@mail.ru", itemRequest.getRequestor().getEmail());
    }

    @Test
    void toDtoResponseWithItems() {
        User user = new User(1L, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(clock));
        ItemDto item = new ItemDto(1L, "nameItem", "descriptionItem", true, 1L);
        ItemRequestDtoResponse itemRequestDtoResponse = mapper.toDtoResponseWithItems(itemRequest, List.of(item));

        assertEquals(1L, itemRequestDtoResponse.getId());
        assertEquals("description", itemRequestDtoResponse.getDescription());
        assertEquals(LocalDateTime.now(clock), itemRequestDtoResponse.getCreated());
        assertEquals(1, itemRequestDtoResponse.getItems().size());
        assertEquals(1L, itemRequestDtoResponse.getItems().get(0).getId());
        assertEquals("nameItem", itemRequestDtoResponse.getItems().get(0).getName());
        assertEquals("descriptionItem", itemRequestDtoResponse.getItems().get(0).getDescription());
        assertEquals(true, itemRequestDtoResponse.getItems().get(0).getAvailable());
        assertEquals(1L, itemRequestDtoResponse.getItems().get(0).getRequestId());
    }

    @Test
    void toDtoResponse() {
        User user = new User(1L, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(clock));
        ItemRequestDtoResponse itemRequestDtoResponse = mapper.toDtoResponse(itemRequest);

        assertEquals(1L, itemRequestDtoResponse.getId());
        assertEquals("description", itemRequestDtoResponse.getDescription());
        assertEquals(LocalDateTime.now(clock), itemRequestDtoResponse.getCreated());
        assertEquals(0, itemRequestDtoResponse.getItems().size());
    }

}