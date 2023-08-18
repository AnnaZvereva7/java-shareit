package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.dto.BookingDtoForOwnerImpl;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoImpl;
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

import javax.validation.constraints.NotBlank;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    @Mock
    private ItemRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentsRepository commentsRepository;

    private ItemServiceImp service;

    @BeforeEach
    public void before() {
        service = new ItemServiceImp(repository, userService,
                bookingRepository, commentsRepository);
    }

    @Test
    void save() {
        Item item = new Item(null, "name", "description", true, null, null);
        Item itemWithOwner = new Item(null, "name", "description", true, 1L, null);
        Item expectedItem = new Item(1L, "name", "description", true, 1L, null);
        when(userService.findById(1L)).thenReturn(new User());
        when(repository.saveAndFlush(itemWithOwner)).thenReturn(expectedItem);
        Item actualItem = service.save(item, 1L);

        assertEquals(1L, actualItem.getId());
        assertEquals("name", actualItem.getName());
        assertEquals("description", actualItem.getDescription());
        assertEquals(true, actualItem.getAvailable());
        assertEquals(1L, actualItem.getOwnerId());
        assertEquals(null, actualItem.getRequestId());
    }

    @Test
    void save_whenWrongOwner() {
        when(userService.findById(anyLong())).thenThrow(new NotFoundException(User.class));

        Throwable thrown = catchThrowable(() -> {
            userService.findById(1L);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void update_WhenAll() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("nameNew");
        itemDto.setDescription("descriptionNew");
        itemDto.setAvailable(false);
        Item item = new Item(1L, "name", "description", true, 1L, null);
        Item expectedItem = new Item(1L, "nameNew", "descriptionNew", false, 1L, null);

        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(repository.saveAndFlush(item)).thenReturn(expectedItem);

        Item actualItem = service.update(itemDto, 1L, 1L);

        assertEquals(1L, actualItem.getId());
        assertEquals("nameNew", actualItem.getName());
        assertEquals("descriptionNew", actualItem.getDescription());
        assertEquals(false, actualItem.getAvailable());
        assertEquals(1L, actualItem.getOwnerId());
        assertEquals(null, actualItem.getRequestId());
    }

    @Test
    void update_WhenName() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("nameNew");
        Item item = new Item(1L, "name", "description", true, 1L, null);
        Item expectedItem = new Item(1L, "nameNew", "description", true, 1L, null);

        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(repository.saveAndFlush(item)).thenReturn(expectedItem);

        Item actualItem = service.update(itemDto, 1L, 1L);

        assertEquals(1L, actualItem.getId());
        assertEquals("nameNew", actualItem.getName());
        assertEquals("description", actualItem.getDescription());
        assertEquals(true, actualItem.getAvailable());
        assertEquals(1L, actualItem.getOwnerId());
        assertEquals(null, actualItem.getRequestId());
    }

    @Test
    void update_whenLimitAccess() {
        ItemDto itemDto = new ItemDto();
        Item item = new Item(1L, "name", "description", true, 1L, null);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        Throwable thrown = catchThrowable(() -> {
            service.update(itemDto, 1L, 2L);
        });

        assertThat(thrown).isInstanceOf(LimitAccessException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void delete() {
        service.delete(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void findById() {
        Item item = new Item(1L, "name", "description", true, 1L, null);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        Item actualItem = service.findById(1L);

        assertEquals(1L, actualItem.getId());
        assertEquals("name", actualItem.getName());
        assertEquals("description", actualItem.getDescription());
        assertEquals(true, actualItem.getAvailable());
        assertEquals(1L, actualItem.getOwnerId());
        assertEquals(null, actualItem.getRequestId());

        when(repository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            service.findById(99L);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByUser() {
        List<Item> expectedItems = List.of(new Item());
        when(userService.findById(anyLong())).thenReturn(new User());
        when(repository.findAllByOwnerId(anyLong(), any(OffsetBasedPageRequest.class))).thenReturn(expectedItems);
        List<Item> actualItems = service.findAllByUser(1L, 0, 20);
        assertEquals(1, actualItems.size());
        assertEquals(expectedItems.get(0), actualItems.get(0));
    }

    @Test
    void lastNextBookingForItem() {
        ItemDtoWithDate itemDto = new ItemDtoWithDate(1L, "name1", "description1", true, null, null, null);
        BookingDtoForOwner lastBooking1 = new BookingDtoForOwnerImpl(1L, 1L, 1L, LocalDateTime.of(2023, 8, 12, 12, 30),
                LocalDateTime.of(2023, 8, 13, 12, 30));
        BookingDtoForOwner nextBooking1 = new BookingDtoForOwnerImpl(1L, 1L, 1L, LocalDateTime.of(2023, 8, 18, 12, 30),
                LocalDateTime.of(2023, 8, 19, 12, 30));

        Clock clock = Clock.fixed(Instant.parse("2023-08-14T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime dateTime = LocalDateTime.now(clock);
        mockStatic(LocalDateTime.class);
        when(LocalDateTime.now()).thenReturn(dateTime);

        when(bookingRepository.findLastBookingForItem(List.of(itemDto.getId()), LocalDateTime.now()))
                .thenReturn(List.of(lastBooking1));
        when(bookingRepository.findNextBookingForItems(List.of(itemDto.getId()), LocalDateTime.now()))
                .thenReturn(List.of(nextBooking1));

        List<ItemDtoWithDate> actualItemsDto = service.lastNextBookingForItem(List.of(itemDto));

        assertEquals(1, actualItemsDto.size());
        assertEquals("name1", actualItemsDto.get(0).getName());
        assertEquals("description1", actualItemsDto.get(0).getDescription());
        assertEquals(true, actualItemsDto.get(0).getAvailable());
        assertEquals(lastBooking1.getId(), actualItemsDto.get(0).getLastBooking().getId());
        assertEquals(lastBooking1.getItemId(), actualItemsDto.get(0).getLastBooking().getItemId());
        assertEquals(lastBooking1.getBookerId(), actualItemsDto.get(0).getLastBooking().getBookerId());
        assertEquals(lastBooking1.getStartDate(), actualItemsDto.get(0).getLastBooking().getStartDate());
        assertEquals(lastBooking1.getEndDate(), actualItemsDto.get(0).getLastBooking().getEndDate());
        assertEquals(nextBooking1.getId(), actualItemsDto.get(0).getNextBooking().getId());
        assertEquals(nextBooking1.getItemId(), actualItemsDto.get(0).getNextBooking().getItemId());
        assertEquals(nextBooking1.getBookerId(), actualItemsDto.get(0).getNextBooking().getBookerId());
        assertEquals(nextBooking1.getStartDate(), actualItemsDto.get(0).getNextBooking().getStartDate());
        assertEquals(nextBooking1.getEndDate(), actualItemsDto.get(0).getNextBooking().getEndDate());
        assertEquals(null, actualItemsDto.get(0).getComments());

    }

    @Test
    void findByText() {
        Item item = new Item(1L, "name", "Description", true, 1L, null);
       String text="descr";
        when(repository.findByTextAndAvailableTrue(anyString(), any(OffsetBasedPageRequest.class))).thenReturn(List.of(item));
    List<Item> expectedItems = List.of(item);
    List<Item> actualItems= service.findByText(text, 0, 20);

    assertEquals(1, actualItems.size());
    assertEquals(expectedItems.get(0), actualItems.get(0));

    }

    @Test
    void addComment() {
        Comment comment = new Comment();
        when(commentsRepository.saveAndFlush(comment)).thenReturn(comment);
        Comment actualComment = service.addComment(comment);
        assertEquals(comment.getClass(), actualComment.getClass());
        verify(commentsRepository, times(1)).saveAndFlush(any(Comment.class));
    }

    @Test
    void getCommentsForItem() {
        CommentDto comment = new CommentDtoImpl();
        ItemDtoWithDate item = new ItemDtoWithDate(1L, "name1", "description1", true, null, null, null);
    when(commentsRepository.findAllCommentsByItemsId(List.of(1L))).thenReturn(List.of(comment));
    ItemDtoWithDate actualItem = service.getCommentsForItem(item);
    assertEquals(List.of(comment), actualItem.getComments());
    }

    @Test
    void getCommentsForItems() {
        CommentDto comment1 = new CommentDtoImpl(1L, 1L, "text1", "author1", LocalDateTime.of(2023, 8, 12, 11, 30));

        ItemDtoWithDate item1 = new ItemDtoWithDate(1L, "name1", "description1", true, null, null, null);
        ItemDtoWithDate item2 = new ItemDtoWithDate(2L, "name2", "description2", true, null, null, null);
        when(commentsRepository.findAllCommentsByItemsId(List.of(1L, 2L))).thenReturn(List.of(comment1));
        List<ItemDtoWithDate> actualItems = service.getCommentsForItems(List.of(item1, item2));
        assertEquals(2, actualItems.size());

        assertEquals(List.of(comment1), actualItems.get(0).getComments());
        assertEquals(null, actualItems.get(1).getComments());

    }
}