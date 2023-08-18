package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;
import ru.practicum.shareit.item.repositiry.CommentsRepository;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.users.UserRepository;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;

    public Item save(Item item, long ownerId) {
        userService.findById(ownerId);
            item.setOwnerId(ownerId);
            return repository.saveAndFlush(item);
    }

    public Item update(ItemDto itemDto, long id, long userId) {
        Item item = findById(id);
        if (item.getOwnerId() == userId) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return repository.saveAndFlush(item);
        } else {
            throw new LimitAccessException("update");
        }
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public Item findById(long id) {
        Optional<Item> itemOptional = repository.findById(id);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException(Item.class);
        } else {
            return itemOptional.get();
        }
    }

    public List<Item> findAllByUser(long userId, int from, int size) {
        userService.findById(userId);
        return repository.findAllByOwnerId(userId, new OffsetBasedPageRequest(from, size));
    }

    @Override
    public List<ItemDtoWithDate> lastNextBookingForItem(List<ItemDtoWithDate> itemsDto) {
        List<Long> itemsId = itemsDto.stream()
                .map(ItemDtoWithDate::getId)
                .collect(toList());
        Map<Long, BookingDtoForOwner> lastBookings =
                bookingRepository.findLastBookingForItem(itemsId, LocalDateTime.now())
                        .stream()
                        .collect(Collectors.toMap(BookingDtoForOwner::getItemId, Function.identity()));
        Map<Long, BookingDtoForOwner> nextBookings =
                bookingRepository.findNextBookingForItems(itemsId, LocalDateTime.now())
                        .stream()
                        .collect(Collectors.toMap(BookingDtoForOwner::getItemId, Function.identity()));
        for (ItemDtoWithDate item : itemsDto) {
            item.setLastBooking(lastBookings.get(item.getId()));
            item.setNextBooking(nextBookings.get(item.getId()));
        }
        return itemsDto;
    }

    @Override
    public List<Item> findByText(String text, int from, int size) {
        return repository.findByTextAndAvailableTrue(text, new OffsetBasedPageRequest(from, size));
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentsRepository.saveAndFlush(comment);
    }

    @Override
    public ItemDtoWithDate getCommentsForItem(ItemDtoWithDate item) {
        item.setComments(commentsRepository.findAllCommentsByItemsId(List.of(item.getId())));
        return item;
    }

    @Override
    public List<ItemDtoWithDate> getCommentsForItems(List<ItemDtoWithDate> items) {
        List<Long> itemsId = items.stream()
                .map(ItemDtoWithDate::getId)
                .collect(toList());
        List<CommentDto> comments = commentsRepository.findAllCommentsByItemsId(itemsId);
        Map<Long, List<CommentDto>> commentsByItem = new HashMap<>();
        for (CommentDto comment : comments) {
            if (commentsByItem.containsKey(comment.getItemId())) {
                commentsByItem.get(comment.getItemId()).add(comment);
            } else {
                List<CommentDto> newList = List.of(comment);
                commentsByItem.put(comment.getItemId(), newList);
            }
        }
        for (ItemDtoWithDate item : items) {
            item.setComments(commentsByItem.get(item.getId()));
        }
        return items;
    }
}
