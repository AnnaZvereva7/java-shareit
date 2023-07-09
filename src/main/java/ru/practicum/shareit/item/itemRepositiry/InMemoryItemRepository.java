package ru.practicum.shareit.item.itemRepositiry;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    int lastId = 0;
    private final ItemMapper itemMapper;

    public InMemoryItemRepository(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public Item save(Item item) {
        lastId += 1;
        item.setId(lastId);
        items.put(lastId, item);
        return items.get(lastId);
    }

    @Override
    public Item updatePartial(Item item, int id) {
        Item itemForUpdate = items.get(id);
        if (item.getName() != null) {
            itemForUpdate = itemForUpdate.withName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate = itemForUpdate.withDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        items.put(id, itemForUpdate);
        return items.get(id);
    }

    @Override
    public void delete(int id) {
        items.remove(id);

    }

    @Override
    public ItemDto findById(int id) {
        return itemMapper.toItemDto(items.get(id));
    }

    @Override
    public Item findItemById(int id) {
        return items.get(id);
    }

    @Override
    public List<ItemDto> findAllByUser(int userId) {
        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId() == userId) {
                userItems.add(itemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    @Override
    public List<ItemDto> findByText(String text) {
        List<ItemDto> itemsWithText = new ArrayList<>();
        if (text.isBlank()) {
            return itemsWithText;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text))
                    && item.getAvailable().equals(Boolean.TRUE)) {
                itemsWithText.add(itemMapper.toItemDto(item));
            }
        }
        return itemsWithText;
    }

}
