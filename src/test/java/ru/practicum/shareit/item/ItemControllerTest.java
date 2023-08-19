package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.dto.BookingDtoForOwnerImpl;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemMapper mapper;

    @Mock
    private ItemService itemService;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;

    BookingDtoForOwnerImpl bookingDtoForOwner = new BookingDtoForOwnerImpl(1L, 1L, 2L,
            LocalDateTime.of(2023, 8, 8, 12, 12),
            LocalDateTime.of(2023, 8, 10, 12, 12));


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void findById_whenOwner_thenWithBooking() throws Exception {
        //given
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        ItemDtoWithDate itemDto = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null, null, null);
        ItemDtoWithDate itemDto2 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null, null, List.of());
        ItemDtoWithDate itemDto3 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, bookingDtoForOwner, null, List.of());

        when(itemService.findById(1L)).thenReturn(item);
        when(mapper.toItemDtoWithDate(item)).thenReturn(itemDto);
        when(itemService.getCommentsForItem(itemDto)).thenReturn(itemDto2);
        when(itemService.lastNextBookingForItem(List.of(itemDto2))).thenReturn(List.of(itemDto3));
        //when
        mvc.perform(get("/items/{itemId}", 1L)
                        .header(Constants.USERID, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto3.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto3.getName())))
                .andExpect(jsonPath("$.description", is(itemDto3.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto3.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto3.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking", is(itemDto3.getNextBooking())))
                .andExpect(jsonPath("$.comments.size()", is(itemDto3.getComments().size())));
    }

    @Test
    void findById_whenNotOwner_thenWithoutBookings() throws Exception {
        //given
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        ItemDtoWithDate itemDto = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null, null, null);
        ItemDtoWithDate itemDto2 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null, null, List.of());

        when(itemService.findById(1L)).thenReturn(item);
        when(mapper.toItemDtoWithDate(item)).thenReturn(itemDto);
        when(itemService.getCommentsForItem(itemDto)).thenReturn(itemDto2);
        //when
        mvc.perform(get("/items/{itemId}", 1L)
                        .header(Constants.USERID, 2L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto2.getName())))
                .andExpect(jsonPath("$.description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto2.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto2.getNextBooking())))
                .andExpect(jsonPath("$.comments.size()", is(itemDto2.getComments().size())));
        verify(itemService, never()).lastNextBookingForItem(List.of(itemDto2));
    }

    @Test
    void findById_whenWrongId_thenThrowsNotFoundException() throws Exception {
        //given
        when(itemService.findById(10L)).thenThrow(new NotFoundException(Item.class));

        //when
        mvc.perform(get("/items/{itemId}", 10L)
                        .header(Constants.USERID, 2L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.item.model.Item not found")));
    }

    @Test
    void findAllByUser() throws Exception {
        //given
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        List<Item> expectedList1 = List.of(item);
        ItemDtoWithDate itemDto = new ItemDtoWithDate(1L, "ItemName", "ItemDescription", Boolean.TRUE, null, null, null);

        CommentDto comment = new CommentDtoImpl(1L, 1L, "text", "authorName", LocalDateTime.of(2023, 8, 14, 11, 30));
        List<CommentDto> comments = List.of(comment);
        ItemDtoWithDate itemDto2 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription", Boolean.TRUE, null, null, comments);
        List<ItemDtoWithDate> expectedList2 = List.of(itemDto2);

        BookingDtoForOwner lastBooking = new BookingDtoForOwnerImpl(1L, 1L, 1L,
                LocalDateTime.of(2023, 8, 11, 12, 30),
                LocalDateTime.of(2023, 8, 12, 12, 20));
        ItemDtoWithDate itemDto3 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription", Boolean.TRUE, lastBooking, null, comments);
        List<ItemDtoWithDate> expectedList3 = List.of(itemDto3);

        when(itemService.findAllByUser(1L, 0, 20)).thenReturn(expectedList1);
        when(mapper.toItemDtoWithDate(item)).thenReturn(itemDto);
        when(itemService.getCommentsForItems(List.of(itemDto))).thenReturn(expectedList2);
        when(itemService.lastNextBookingForItem(expectedList2)).thenReturn(expectedList3);
        //when
        mvc.perform(get("/items")
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(expectedList3.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(expectedList3.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(expectedList3.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(expectedList3.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.itemId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].nextBooking", is(nullValue())))
                .andExpect(jsonPath("$[0].comments.size()", is(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].comments[0].itemId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is("text")));

    }

    @Test
    void findAllByUser_whenWrongUser() throws Exception {
        //given
        when(itemService.findAllByUser(99L, 0, 20)).thenThrow(new NotFoundException(User.class));
        //when
        mvc.perform(get("/items")
                        .header(Constants.USERID, 99L)
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByUser_whenEmpty() throws Exception {
        //given
        List<Item> expectedList1 = List.of();
        List<ItemDtoWithDate> expectedList2 = List.of();

        when(itemService.findAllByUser(1L, 0, 20)).thenReturn(expectedList1);
        when(itemService.getCommentsForItems(expectedList2)).thenReturn(expectedList2);
        when(itemService.lastNextBookingForItem(expectedList2)).thenReturn(expectedList2);
        //when
        mvc.perform(get("/items")
                        .header(Constants.USERID, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }


    @Test
    void save() throws Exception {
        //given
        Item item = new Item(null, "name", "description", true, null, null);
        ItemDto itemDto = new ItemDto(null, "name", "description", true, null);
        Item expectedItem = new Item(1L, "name", "description", true, 1L, null);
        ItemDto expectedItemDto = new ItemDto(1L, "name", "description", true, null);

        when(mapper.fromItemDto(itemDto)).thenReturn(item);
        when(itemService.save(item, 1L)).thenReturn(expectedItem);
        when(mapper.toItemDto(expectedItem)).thenReturn(expectedItemDto);
        //when
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk());
    }

    @Test
    void save_whenWrongDtoName() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(null, "   ", "description", true, null);

        //when
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
        verify(itemService, never()).save(any(Item.class), anyLong());
    }

    @Test
    void update_whenOk() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "nameNew", "descriptionNew", null, null);
        Item expectedItem = new Item(1L, "nameNew", "descriptionNew", true, 1L, null);
        ItemDto expectedItemDto = new ItemDto(1L, "nameNew", "descriptionNew", true, null);

        when(itemService.update(itemDto, 1L, 1L)).thenReturn(expectedItem);
        when(mapper.toItemDto(expectedItem)).thenReturn(expectedItemDto);
        //when
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("nameNew")))
                .andExpect(jsonPath("$.description", is("descriptionNew")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void update_whenWrongDto() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "nameNewNamenameNewNamenameNewNamenameNewNamenameNewName", "descriptionNew", null, null);

        //when
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_whenWrongItemId() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(99L, "nameNew", "descriptionNew", null, null);

        when(itemService.update(itemDto, 99L, 1L)).thenThrow(new NotFoundException(Item.class));

        //when
        mvc.perform(patch("/items/{itemId}", 99L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.item.model.Item not found")));
    }

    @Test
    void update_whenLimitAccess() throws Exception {
        //given
        ItemDto itemDto = new ItemDto(1L, "nameNew", "descriptionNew", null, null);

        when(itemService.update(itemDto, 1L, 2L)).thenThrow(new LimitAccessException("update"));

        //when
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(Constants.USERID, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Limit access to update")));
    }


    @Test
    void findByText_whenTextIsBlank() throws Exception {
        //when
        mvc.perform(get("/items/search")
                        .param("text", "   ")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void findByText_whenOk() throws Exception {
        //given
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        List<Item> expectedList1 = List.of(item);
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", Boolean.TRUE, null);

        when(itemService.findByText("description", 0, 20)).thenReturn(expectedList1);
        when(mapper.toItemDto(item)).thenReturn(itemDto);
        //when
        mvc.perform(get("/items/search")
                        .param("text", "description")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("ItemName")))
                .andExpect(jsonPath("$[0].description", is("ItemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].requestId", is(nullValue())));
    }

    @Test
    void addComment() throws Exception {
        //given
        User author = new User(1L, "name", "email@mail.ru");
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        CommentDtoRequest commentDto = new CommentDtoRequest("someText");
        Comment comment = new Comment(null, "someText", item, author, LocalDateTime.of(2023, 8, 12, 12, 20));
        Comment expectedComment = new Comment(1L, "someText", item, author, LocalDateTime.of(2023, 8, 12, 12, 20));
        CommentDtoResponse expectedCommentDto = new CommentDtoResponse(1L, "someText", 1L, "name",
                LocalDateTime.of(2023, 8, 12, 12, 20));

        when(userService.findById(1L)).thenReturn(author);
        when(itemService.findById(1L)).thenReturn(item);
        when(bookingService.checkForComment(1L, 1L)).thenReturn(true);
        when(commentMapper.toComment(commentDto, item, author)).thenReturn(comment);
        when(itemService.addComment(comment)).thenReturn(expectedComment);
        when(commentMapper.toCommentDto(expectedComment)).thenReturn(expectedCommentDto);

        //when
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(Constants.USERID, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is("someText")))
                .andExpect(jsonPath("$.authorId", is(1L), Long.class))
                .andExpect(jsonPath("$.authorName", is("name")))
                .andExpect(jsonPath("$.created", is("12-08-2023 12:20:00")));
    }

    @Test
    void addComment_whenWrongUser() throws Exception {
        //given
        CommentDtoRequest commentDto = new CommentDtoRequest("someText");
        when(userService.findById(1L)).thenThrow(new NotFoundException(User.class));

        //when
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(Constants.USERID, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.users.model.User not found")));
    }

    @Test
    void addComment_whenWrongItem() throws Exception {
        //given
        CommentDtoRequest commentDto = new CommentDtoRequest("someText");
        User author = new User(1L, "name", "email@mail.ru");
        when(userService.findById(1L)).thenReturn(author);
        when(itemService.findById(1L)).thenThrow(new NotFoundException(Item.class));
        //when
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(Constants.USERID, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity class ru.practicum.shareit.item.model.Item not found")));
    }

    @Test
    void addComment_whenNoItemToCommented() throws Exception {
        //given
        User author = new User(1L, "name", "email@mail.ru");
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        CommentDtoRequest commentDto = new CommentDtoRequest("someText");

        when(userService.findById(1L)).thenReturn(author);
        when(itemService.findById(1L)).thenReturn(item);
        when(bookingService.checkForComment(1L, 1L)).thenThrow(new NotAvailableException("for comment"));

        //when
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(Constants.USERID, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("not available for comment")));
    }
}