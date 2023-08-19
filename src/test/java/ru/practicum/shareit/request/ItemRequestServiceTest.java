package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.users.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private ItemMapper itemMapper;

    private ItemRequestService service;

    @BeforeEach
    public void before() {
        service = new ItemRequestService(repository, itemRepository, mapper, itemMapper);
    }

    @Test
    void save() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 10, 12, 00);
        User requestor = new User(1L, "userName", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(null, "ItemDescription", requestor, created);
        ItemRequest expectedItemRequest = new ItemRequest(1L, "ItemDescription", requestor, created);
        when(repository.saveAndFlush(itemRequest)).thenReturn(expectedItemRequest);
        ItemRequest actualItemRequest = service.save(itemRequest);
        assertEquals(expectedItemRequest, actualItemRequest);
    }

    @Test
    void findByUser() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 10, 12, 00);
        User requestor = new User(1L, "userName", "email@mail.ru");
        ItemRequest itemRequest1 = new ItemRequest(1L, "ItemDescription1", requestor, created);
        ItemRequest itemRequest2 = new ItemRequest(2L, "ItemDescription2", requestor, created.plusDays(1));
        when(repository.findByRequestorId(1L)).thenReturn(List.of(itemRequest1, itemRequest2));
        when(mapper.toDtoResponse(itemRequest1)).thenReturn(new ItemRequestDtoResponse(1L, "ItemDescription1", created, List.of()));
        when(mapper.toDtoResponse(itemRequest2)).thenReturn(new ItemRequestDtoResponse(2L, "ItemDescription2", created.plusDays(1), List.of()));
        Item item1 = new Item(1L, "name", "description", true, 2L, 1L);
        Item item2 = new Item(2L, "name2", "description2", true, 3L, 1L);
        when(itemRepository.findByRequestIdIn(List.of(1L, 2L))).thenReturn(List.of(item1, item2));
        when(itemMapper.toItemDto(item1)).thenReturn(new ItemDto(1L, "name", "description", true, 1L));
        when(itemMapper.toItemDto(item2)).thenReturn(new ItemDto(2L, "name2", "description2", true, 1L));
        List<ItemRequestDtoResponse> response = service.findByUser(1L);

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("ItemDescription1", response.get(0).getDescription());
        assertEquals(created, response.get(0).getCreated());
        assertEquals(2, response.get(0).getItems().size());
        assertEquals(1L, response.get(0).getItems().get(0).getId());
        assertEquals("name", response.get(0).getItems().get(0).getName());
        assertEquals(2L, response.get(0).getItems().get(1).getId());
        assertEquals("name2", response.get(0).getItems().get(1).getName());
        assertEquals(2L, response.get(1).getId());
        assertEquals("ItemDescription2", response.get(1).getDescription());
        assertEquals(created.plusDays(1), response.get(1).getCreated());
        assertEquals(0, response.get(1).getItems().size());
    }

    @Test
    void findByUser_whenNoItems() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 10, 12, 00);
        User requestor = new User(1L, "userName", "email@mail.ru");
        ItemRequest itemRequest1 = new ItemRequest(1L, "ItemDescription1", requestor, created);
        ItemRequest itemRequest2 = new ItemRequest(2L, "ItemDescription2", requestor, created.plusDays(1));
        when(repository.findByRequestorId(1L)).thenReturn(List.of(itemRequest1, itemRequest2));
        when(mapper.toDtoResponse(itemRequest1)).thenReturn(new ItemRequestDtoResponse(1L, "ItemDescription1", created, List.of()));
        when(mapper.toDtoResponse(itemRequest2)).thenReturn(new ItemRequestDtoResponse(2L, "ItemDescription2", created.plusDays(1), List.of()));
        when(itemRepository.findByRequestIdIn(List.of(1L, 2L))).thenReturn(List.of());
        List<ItemRequestDtoResponse> response = service.findByUser(1L);

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("ItemDescription1", response.get(0).getDescription());
        assertEquals(created, response.get(0).getCreated());
        assertEquals(0, response.get(0).getItems().size());
        assertEquals(0, response.get(1).getItems().size());
    }

    @Test
    void findByUser_whenEmpty() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 10, 12, 00);
        User requestor = new User(1L, "userName", "email@mail.ru");
        when(repository.findByRequestorId(1L)).thenReturn(List.of());

        List<ItemRequestDtoResponse> response = service.findByUser(1L);

        assertEquals(0, response.size());
    }

    @Test
    void findByRequestorIdNot() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 10, 12, 00);
        User requestor = new User(1L, "userName", "email@mail.ru");
        ItemRequest itemRequest1 = new ItemRequest(1L, "ItemDescription1", requestor, created);
        ItemRequest itemRequest2 = new ItemRequest(2L, "ItemDescription2", requestor, created.plusDays(1));
        when(repository.findByRequestorIdNot(anyLong(), any(OffsetBasedPageRequest.class))).thenReturn(List.of(itemRequest1, itemRequest2));
        when(mapper.toDtoResponse(itemRequest1)).thenReturn(new ItemRequestDtoResponse(1L, "ItemDescription1", created, List.of()));
        when(mapper.toDtoResponse(itemRequest2)).thenReturn(new ItemRequestDtoResponse(2L, "ItemDescription2", created.plusDays(1), List.of()));
        Item item1 = new Item(1L, "name", "description", true, 2L, 1L);
        Item item2 = new Item(2L, "name2", "description2", true, 3L, 1L);
        when(itemRepository.findByRequestIdIn(List.of(1L, 2L))).thenReturn(List.of(item1, item2));
        when(itemMapper.toItemDto(item1)).thenReturn(new ItemDto(1L, "name", "description", true, 1L));
        when(itemMapper.toItemDto(item2)).thenReturn(new ItemDto(2L, "name2", "description2", true, 1L));
        List<ItemRequestDtoResponse> response = service.findByRequestorIdNot(2L, 0, 20);

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("ItemDescription1", response.get(0).getDescription());
        assertEquals(created, response.get(0).getCreated());
        assertEquals(2, response.get(0).getItems().size());
        assertEquals(1L, response.get(0).getItems().get(0).getId());
        assertEquals("name", response.get(0).getItems().get(0).getName());
        assertEquals(2L, response.get(0).getItems().get(1).getId());
        assertEquals("name2", response.get(0).getItems().get(1).getName());
        assertEquals(2L, response.get(1).getId());
        assertEquals("ItemDescription2", response.get(1).getDescription());
        assertEquals(created.plusDays(1), response.get(1).getCreated());
        assertEquals(0, response.get(1).getItems().size());
    }

    @Test
    void findById() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 10, 12, 00);
        User requestor = new User(1L, "userName", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "ItemDescription1", requestor, created);
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequest actualItemRequest = service.findById(1L);
        assertEquals(1L, actualItemRequest.getId());
        assertEquals("ItemDescription1", actualItemRequest.getDescription());
        assertEquals(created, actualItemRequest.getCreated());
        assertEquals(requestor, actualItemRequest.getRequestor());
    }

    @Test
    void findById_whenEmpty() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            service.findById(99L);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }
}