package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositiry.CommentsRepository;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;

    public ItemServiceImp(@Qualifier("DBItem") ItemRepository itemRepository,
                          @Qualifier("DBUsers") UserRepository userRepository,
                          BookingRepository bookingRepository,
                          CommentsRepository commentsRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentsRepository = commentsRepository;
    }

    public Item save(Item item, long ownerId) {
        item.setOwnerId(ownerId);
        userRepository.containId(item.getOwnerId());
        return itemRepository.save(item, ownerId);
    }

    public Item updatePartial(Item item, long id, long userId) {
        if (itemRepository.findById(id).getOwnerId() == userId) {
            return itemRepository.update(item, id);
        } else {
            throw new LimitAccessException();
        }
    }

    public void delete(long id) {
        itemRepository.delete(id);
    }

    public Item findById(long id) {
        return itemRepository.findById(id);
    }

    public List<Item> findAllByUser(long userId) {
        userRepository.containId(userId);
        return itemRepository.findAllByUser(userId);
    }

    @Override
    public ItemDtoWithDate lastNextBookingForItem(ItemDtoWithDate item) {
        long itemId = item.getId();

        item.setLastBooking(bookingRepository.findLastBookingPeriodForItem(itemId, LocalDateTime.now()));
        item.setNextBooking(bookingRepository.findNextBookingPeriodForItem(itemId, LocalDateTime.now()));
        return item;
    }

    public List<Item> findByText(String text) {
        return itemRepository.findByText(text);
    }

    public Comment addComment(Comment comment) {
        return commentsRepository.saveAndFlush(comment);
    }

    public ItemDtoWithDate getComments(ItemDtoWithDate item) {
        item.setComments(commentsRepository.findAllCommentsByItemId(item.getId()));
        return item;
    }
}
