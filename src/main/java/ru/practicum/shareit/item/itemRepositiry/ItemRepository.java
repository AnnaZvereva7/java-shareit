package ru.practicum.shareit.item.itemRepositiry;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item updatePartial(Item item, int id);

    void delete(int id);

    Item findById(int id);

    Item findItemById(int id);

    List<Item> findAllByUser(int userId);

    List<Item> findByText(String text);
}
