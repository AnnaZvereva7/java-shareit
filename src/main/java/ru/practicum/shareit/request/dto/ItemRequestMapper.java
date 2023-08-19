package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.users.model.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final Clock clock;

    public ItemRequest fromDto(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest(null, itemRequestDto.getDescription(), user, LocalDateTime.now(clock));
        return itemRequest;
    }

    public ItemRequestDtoResponse toDtoResponseWithItems(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDtoResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }

    public ItemRequestDtoResponse toDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), new ArrayList<>());
    }

}
