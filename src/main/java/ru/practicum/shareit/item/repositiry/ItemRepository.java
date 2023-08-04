package ru.practicum.shareit.item.repositiry;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item, long ownerId);

    Item update(Item item, long id);

    void delete(long id);

    Item findById(long id);

    List<Item> findAllByUser(long userId);

    List<Item> findByText(String text);
}
