package ru.practicum.shareit.items;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.common.Marker;
import ru.practicum.shareit.items.dto.CommentDtoRequest;
import ru.practicum.shareit.items.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable Long itemId,
                                           @RequestHeader(Constants.USERID) Long userId) {
        log.info("Get item with id={} by user with id={}", itemId, userId);
        return itemClient.findById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestHeader(Constants.USERID) Long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Find all items with ownerId={}, from={}, size={}", userId, from, size);
        return itemClient.findAllByUser(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Validated(Marker.OnCreate.class) ItemDto itemDto,
                                       @RequestHeader(Constants.USERID) Long userId) {
        log.info("Save new item {} by user with id={}", itemDto, userId);
        return itemClient.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody @Validated(Marker.OnUpdate.class) ItemDto itemDto,
                                         @RequestHeader(Constants.USERID) Long userId,
                                         @PathVariable Long itemId) {
        log.info("Update item with id={} by user with id={} to {}", itemId, userId, itemDto);
        return itemClient.update(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    //pagination
    public ResponseEntity<Object> findByText(@RequestParam String text,
                                             @RequestHeader(Constants.USERID) Long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "20") int size) {
        if (text.isBlank()) {
            log.info("Text is blank");
            return ResponseEntity.status(HttpStatus.OK).body(List.of());
        }
        log.info("Find items with text={} by user with id={}, from={}, size={}", text, userId, from, size);
        return itemClient.findByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody @Valid CommentDtoRequest commentDto,
                                             @RequestHeader(Constants.USERID) Long userId) {
        log.info("Add new comment {} to item with id={} by user with id={} ", commentDto, itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
