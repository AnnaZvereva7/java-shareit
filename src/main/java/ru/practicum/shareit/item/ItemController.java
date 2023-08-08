package ru.practicum.shareit.item;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/items",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final ItemMapper mapper;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public ItemController(ItemService itemService,
                          BookingService bookingService,
                          ItemMapper mapper,
                          UserService userService,
                          CommentMapper commentMapper) {
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.mapper = mapper;
        this.userService = userService;
        this.commentMapper = commentMapper;
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDate findById(@PathVariable Long itemId, @RequestHeader(Constants.USERID) Long userId) {
        Item item = itemService.findById(itemId);
        ItemDtoWithDate itemDto = mapper.toItemDtoWithDate(item);
        itemDto = itemService.getCommentsForItem(itemDto);
        if (item.getOwnerId() == userId) {
            return (itemService.lastNextBookingForItem(List.of(itemDto))).get(0);
        } else {
            return itemDto;
        }
    }

    @GetMapping
    public List<ItemDtoWithDate> findAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDtoWithDate> itemsDto = itemService.findAllByUser(userId)
                .stream()
                .map(mapper::toItemDtoWithDate)
                .collect(toList());
        itemsDto = itemService.getCommentsForItems(itemsDto);
        itemsDto = itemService.lastNextBookingForItem(itemsDto);
        return itemsDto;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto save(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return mapper.toItemDto(itemService.save(mapper.fromItemDto(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ItemDto update(@RequestBody @Valid ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId) {
        Item updatedItem = itemService.update(itemDto, itemId, userId);
        return mapper.toItemDto(updatedItem);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(defaultValue = "") String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemService.findByText(text.toLowerCase())
                    .stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@PathVariable Long itemId,
                                         @RequestBody @Valid CommentDtoRequest commentDto,
                                         @RequestHeader(Constants.USERID) Long userId) {
        User author = userService.findById(userId);
        Item item = itemService.findById(itemId);
        bookingService.checkForComment(userId, itemId);
        return commentMapper.toCommentDto(itemService.addComment(commentMapper.toComment(commentDto, item, author)));
    }


}
