package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Item item, long ownerId);

    Item update(ItemDto itemDto, long id, long userId);

    void delete(long id);

    Item findById(long id);

    List<Item> findAllByUser(long userId);

    List<Item> findByText(String text);

    List<ItemDtoWithDate> lastNextBookingForItem(List<ItemDtoWithDate> itemsDto);

    Comment addComment(Comment comment);

    ItemDtoWithDate getCommentsForItem(ItemDtoWithDate item);
    public List<ItemDtoWithDate> getCommentsForItems(List<ItemDtoWithDate> items);
}
