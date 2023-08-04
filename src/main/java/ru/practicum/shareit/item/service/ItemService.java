package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Item item, long ownerId);

    Item updatePartial(Item item, long id, long userId);

    void delete(long id);

    Item findById(long id);

    List<Item> findAllByUser(long userId);

    List<Item> findByText(String text);

    public ItemDtoWithDate lastNextBookingForItem(ItemDtoWithDate item);

    public Comment addComment(Comment comment);

    public ItemDtoWithDate getComments(ItemDtoWithDate item);
}
