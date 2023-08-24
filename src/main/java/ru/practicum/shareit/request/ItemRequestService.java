package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    public ItemRequest save(ItemRequest itemRequest) {
        return itemRequestRepository.saveAndFlush(itemRequest);
    }

    public List<ItemRequestDtoResponse> findByUser(Long requestorId) {
        List<ItemRequest> itemRequestsByRequestorId = itemRequestRepository.findByRequestorId(requestorId);
        return fromItemRequestToResponse(itemRequestsByRequestorId);
    }

    public List<ItemRequestDtoResponse> findByRequestorIdNot(Long userId, int from, int size) {
        return fromItemRequestToResponse(itemRequestRepository
                .findByRequestorIdNot(userId, new OffsetBasedPageRequest(from, size)));
    }

    private List<ItemRequestDtoResponse> fromItemRequestToResponse(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            return List.of();
        }
        List<ItemRequestDtoResponse> requestsDto = itemRequests
                .stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList());
        List<Long> ids = requestsDto
                .stream()
                .map(ItemRequestDtoResponse -> ItemRequestDtoResponse.getId())
                .collect(Collectors.toList());
        Map<Long, List<ItemDto>> itemsMap = new HashMap<>();
        List<ItemDto> itemsList = itemRepository.findByRequestIdIn(ids)
                .stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemsList) {
            Long requestId = itemDto.getRequestId();
            List<ItemDto> thisListItemDto = itemsMap.getOrDefault(requestId, new ArrayList<>());
            thisListItemDto.add(itemDto);
            itemsMap.put(requestId, thisListItemDto);
        }
        for (ItemRequestDtoResponse itemRequest : requestsDto) {
            List<ItemDto> thisListItemDto = itemsMap.get(itemRequest.getId());
            itemRequest.setItems(thisListItemDto == null ? List.of() : thisListItemDto);
        }
        return requestsDto;
    }

    public ItemRequest findById(Long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException(ItemRequest.class));
    }

}
