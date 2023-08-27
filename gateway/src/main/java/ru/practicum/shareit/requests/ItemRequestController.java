package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader(Constants.USERID) Long userId,
                                       @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Save request {} by user with id={}", itemRequestDto, userId);
        return itemRequestClient.save(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findByUser(@RequestHeader(Constants.USERID) Long userId) {
        log.info("Find all requests by owner with id={}", userId);
        return itemRequestClient.findByUser(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader(Constants.USERID) Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Find all requests when owner not with id={}, from={}, size={}", userId, from,size);
        return itemRequestClient.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId,
                                           @RequestHeader(Constants.USERID) Long userId) {
        log.info("Find request with id={} by user with id={}", requestId, userId);
        return itemRequestClient.findById(userId, requestId);
    }
}
