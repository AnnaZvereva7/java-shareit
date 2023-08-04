package ru.practicum.shareit.item.repositiry;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("InMemoryItem")
public class InMemoryItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    long lastId = 0;

    @Override
    public Item save(Item item, long ownerId) {
        lastId += 1;
        item.setId(lastId);
        items.put(lastId, item);
        return items.get(lastId);
    }

    @Override
    public Item update(Item item, long id) {
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
    public void delete(long id) {
        items.remove(id);

    }

    @Override
    public Item findById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAllByUser(long userId) {
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
