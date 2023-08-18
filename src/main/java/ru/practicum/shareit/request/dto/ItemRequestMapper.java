package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.users.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest fromDto(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest(null, itemRequestDto.getDescription(), user, LocalDateTime.now());
        return itemRequest;
    }

    public ItemRequestDtoResponse toDtoResponse(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDtoResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }

    public ItemRequestDtoResponse toDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), new ArrayList<>());
    }

}
