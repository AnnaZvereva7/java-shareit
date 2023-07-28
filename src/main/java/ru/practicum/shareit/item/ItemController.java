package ru.practicum.shareit.item;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.itemRepositiry.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/items",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    public ItemController(ItemRepository itemRepository, ItemService itemService, ItemMapper mapper) {
        this.itemService = itemService;
        this.mapper = mapper;
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable int itemId) {
        return mapper.toItemDto(itemService.findById(itemId));
    }

    @GetMapping
    public List<ItemDto> findAllByUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAllByUser(userId)
                .stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto save(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return mapper.toItemDto(itemService.save(mapper.fromItemDto(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ItemDto updatePartial(@RequestBody @Valid ItemDto itemDto,
                                 @RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int itemId) {
        return mapper.toItemDto(itemService.updatePartial(mapper.fromItemDto(itemDto), itemId, userId));
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(defaultValue = "") String text) {
        return itemService.findByText(text.toLowerCase())
                .stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }
}
