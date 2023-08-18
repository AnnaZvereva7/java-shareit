package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepositoryImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestRepositoryImpl itemRequestRepositoryImpl;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    public ItemRequest save(ItemRequest itemRequest) {
        return itemRequestRepository.saveAndFlush(itemRequest);
    }

    public List<ItemRequestDtoResponse> findByUser(Long requestorId) {
        List<ItemRequest> itemRequestsByRequestorId = itemRequestRepository.findByRequestorId(requestorId, Sort.by(Sort.Direction.DESC, "created"));
        return fromItemRequestToResponse(itemRequestsByRequestorId);
    }

    public List<ItemRequestDtoResponse> findByRequestorIdNot(Long userId, int from, int size) {
        return fromItemRequestToResponse(itemRequestRepositoryImpl
                .findByRequestorIdNot(userId, from, size));
    }

    public List<ItemRequestDtoResponse> fromItemRequestToResponse(List<ItemRequest> itemRequests) {
        if (itemRequests == null) {
            return List.of();
        }
        List<ItemRequestDtoResponse> requestsDto = itemRequests
                .stream()
                .map(itemRequest -> mapper.toDtoResponse(itemRequest))
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
            if (itemsMap.containsKey(itemDto.getRequestId())) {
                itemsMap.get(itemDto.getRequestId()).add(itemDto);
            } else {
                itemsMap.put(itemDto.getRequestId(), List.of(itemDto));
            }
            // itemsMap.computeIfAbsent(itemDto.getRequestId(), k -> new ArrayList<>());
        }
        for (ItemRequestDtoResponse itemRequest : requestsDto) {
            itemRequest.setItems(itemsMap.get(itemRequest.getId()) == null ? List.of() : itemsMap.get(itemRequest.getId()));
        }
        return requestsDto;
    }

    public ItemRequest findById(Long id) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(id);
        if (itemRequest.isEmpty()) {
            throw new NotFoundException(ItemRequest.class);
        }
        return itemRequest.get();
    }

}
