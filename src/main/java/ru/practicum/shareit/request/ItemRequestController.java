package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemRepository itemRepository;

    @PostMapping
    public ItemRequest save(@RequestHeader(Constants.USERID) Long userId,
                            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        User user = userService.findById(userId);
        ItemRequest itemRequest = mapper.fromDto(itemRequestDto, user);
        return itemRequestService.save(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> findByUser(@RequestHeader(Constants.USERID) Long userId) {
        userService.findById(userId);
        return itemRequestService.findByUser(userId);
    }

    @GetMapping(value = "/all")
    //pagination
    public List<ItemRequestDtoResponse> findAllRequests(@RequestHeader(Constants.USERID) Long userId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "20") @Positive int size) {
        userService.findById(userId);
        return itemRequestService.findByRequestorIdNot(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse findById(@PathVariable @Positive Long requestId,
                                           @RequestHeader(Constants.USERID) Long userId) {
        userService.findById(userId);
        List<ItemDto> items = itemRepository.findByRequestIdIn(List.of(requestId))
                .stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return mapper.toDtoResponse(itemRequestService.findById(requestId), items);
    }

}
