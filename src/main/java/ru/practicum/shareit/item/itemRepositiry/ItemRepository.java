package ru.practicum.shareit.item.itemRepositiry;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item updatePartial(Item item, int id);

    void delete(int id);

    ItemDto findById(int id);

    Item findItemById(int id);

    List<ItemDto> findAllByUser(int userId);

    List<ItemDto> findByText(String text);
}
