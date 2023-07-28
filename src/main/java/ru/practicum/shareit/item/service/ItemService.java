package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Item item, int ownerId);

    Item updatePartial(Item item, int id, int userId);

    void delete(int id);

    Item findById(int id);

    List<Item> findAllByUser(int userId);

    List<Item> findByText(String text);
}
