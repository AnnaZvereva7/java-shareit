package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemRepositiry.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemRepository itemRepository, ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable int itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> findAllByUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAllByUser(userId);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Item save(@RequestBody @Valid Item item, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.save(item.withOwnerId(userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public Item updatePartial(@RequestBody @Valid Item item,
                              @RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable int itemId) {
        return itemService.updatePartial(item, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(defaultValue = "") String text) {
        return itemService.findByText(text.toLowerCase());
    }
}
