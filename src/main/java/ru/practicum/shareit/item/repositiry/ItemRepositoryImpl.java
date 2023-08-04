package ru.practicum.shareit.item.repositiry;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.DBUserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Qualifier("DBItem")
public class ItemRepositoryImpl implements ItemRepository {
    private final DBItemRepository repository;
    private final DBUserRepository userRepository;

    public ItemRepositoryImpl(DBItemRepository repository, DBUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Item save(Item item, long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        } else {
            return repository.save(item);
        }
    }

    @Override
    public Item update(Item item, long id) {
        Item itemForUpdate = repository.findById(id).get();
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        repository.saveAndFlush(itemForUpdate);
        return repository.findById(id).get();
    }


    @Transactional
    private boolean itemIsExist(long id) {
        if (repository.findById(id).isPresent()) {
            return true;
        } else {
            throw new NotFoundException("Объект не найден");
        }
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public Item findById(long id) {
        Optional<Item> itemOptional = repository.findById(id);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("объект отсутствует");
        } else {
            return itemOptional.get();
        }
    }

    @Override
    public List<Item> findAllByUser(long userId) {
        return repository.findByOwnerId(userId);
    }

    @Override
    public List<Item> findByText(String text) {
        return repository.findByNameOrDescriptionContainingIgnoreCase(text.toLowerCase(), text.toLowerCase())
                .stream()
                .filter(item -> item.getAvailable() == true)
                .collect(Collectors.toList());
    }

    boolean isUserExist(long user_id) {
        return false;
    }
}
