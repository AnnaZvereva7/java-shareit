package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemRepositiry.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.userRepository.UserRepository;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Item save(Item item) {
        userRepository.containId(item.getOwnerId());
        return itemRepository.save(item);
    }

    public Item updatePartial(Item item, int id, int userId) {
        if (itemRepository.findItemById(id).getOwnerId() == userId) {
            return itemRepository.updatePartial(item, id);
        } else {
            throw new LimitAccessException();
        }
    }

    public void delete(int id) {
        itemRepository.delete(id);
    }

    public ItemDto findById(int id) {
        return itemRepository.findById(id);
    }

    public List<ItemDto> findAllByUser(int userId) {
        userRepository.containId(userId);
        return itemRepository.findAllByUser(userId);
    }

    public List<ItemDto> findByText(String text) {
        return itemRepository.findByText(text);
    }
}
