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
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
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
    public ItemDtoWithDate findById(@PathVariable long itemId, @RequestHeader(Constants.USERID) long userId) {
        Item item = itemService.findById(itemId);
        ItemDtoWithDate itemDto = mapper.toItemDtoWithDate(item);
        itemDto = itemService.getComments(itemDto);
        if (item.getOwnerId() == userId) {
            return (itemService.lastNextBookingForItem(itemDto));
        } else {
            return itemDto;
        }
    }

    @GetMapping
    public List<ItemDtoWithDate> findAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllByUser(userId)
                .stream()
                .map(mapper::toItemDtoWithDate)
                .map(itemService::lastNextBookingForItem)
                .map(itemService::getComments)
                .collect(toList());
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto save(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return mapper.toItemDto(itemService.save(mapper.fromItemDto(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ItemDto updatePartial(@RequestBody @Valid ItemDto itemDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId) {
        Item itemUpdate = mapper.fromItemDto(itemDto);
        Item updatedItem = itemService.updatePartial(itemUpdate, itemId, userId);
        return mapper.toItemDto(updatedItem);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(defaultValue = "") String text) {
        if (text.equals("")) {
            return new ArrayList<>();
        } else {
            return itemService.findByText(text.toLowerCase())
                    .stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoClass addComment(@PathVariable long itemId,
                                      @RequestBody @Valid CommentDtoClass commentDto,
                                      @RequestHeader(Constants.USERID) long userId) {
        bookingService.checkForComment(userId, itemId);
        commentDto.setAuthor_id(userId);
        return commentMapper.toCommentDto(itemService.addComment(commentMapper.toComment(commentDto, itemId)));
    }


}
