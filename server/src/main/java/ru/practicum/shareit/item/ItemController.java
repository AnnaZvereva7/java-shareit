package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/items",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final ItemMapper mapper;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final ItemRequestService itemRequestService;


    @GetMapping("/{itemId}")
    public ItemDtoWithDate findById(@PathVariable Long itemId,
                                    @RequestHeader(Constants.USERID) Long userId) {
        Item item = itemService.findById(itemId);
        ItemDtoWithDate itemDto = mapper.toItemDtoWithDate(item);
        itemDto = itemService.getCommentsForItem(itemDto);
        if (item.getOwnerId().equals(userId)) {
            return (itemService.lastNextBookingForItem(List.of(itemDto))).get(0);
        } else {
            return itemDto;
        }
    }

    @GetMapping
    //pagination
    public List<ItemDtoWithDate> findAllByUser(@RequestHeader(Constants.USERID) Long userId,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        List<ItemDtoWithDate> itemsDto = itemService.findAllByUser(userId, from, size)
                .stream()
                .map(mapper::toItemDtoWithDate)
                .collect(toList());
        itemsDto = itemService.getCommentsForItems(itemsDto);
        itemsDto = itemService.lastNextBookingForItem(itemsDto);
        return itemsDto;
    }

    @PostMapping
    public ItemDto save(@RequestBody ItemDto itemDto,
                        @RequestHeader(Constants.USERID) Long userId) {
        return mapper.toItemDto(itemService.save(mapper.fromItemDto(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader(Constants.USERID) Long userId,
                          @PathVariable Long itemId) {
        Item updatedItem = itemService.update(itemDto, itemId, userId);
        return mapper.toItemDto(updatedItem);
    }

    @GetMapping("/search")
    //pagination
    public List<ItemDto> findByText(@RequestParam String text,
                                    @RequestParam int from,
                                    @RequestParam int size) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemService.findByText(text.toLowerCase(), from, size)
                    .stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@PathVariable Long itemId,
                                         @RequestBody CommentDtoRequest commentDto,
                                         @RequestHeader(Constants.USERID) Long userId) {
        User author = userService.findById(userId);
        Item item = itemService.findById(itemId);
        bookingService.checkForComment(userId, itemId);
        return commentMapper.toCommentDto(itemService.addComment(commentMapper.toComment(commentDto, item, author)));
    }
}
