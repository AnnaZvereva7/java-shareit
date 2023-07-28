package ru.practicum.shareit.item.itemRepositiry;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    int lastId = 0;

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
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
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
    public Item findById(int id) {
        return items.get(id);
    }

    @Override
    public Item findItemById(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAllByUser(int userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> findByText(String text) {
        List<Item> itemsWithText = new ArrayList<>();
        if (text.isBlank()) {
            return itemsWithText;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text))
                    && item.getAvailable().equals(Boolean.TRUE)) {
                itemsWithText.add(item);
            }
        }
        return itemsWithText;
    }

}
