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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.users.UserController;
import ru.practicum.shareit.users.dto.UserMapper;
import ru.practicum.shareit.users.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    BookingDtoForOwner bookingDtoForOwner=new BookingDtoForOwner() {
        @Override
        public long getId() {
            return 1L;
        }
        @Override
        public long getItemId() {
            return 1L;
        }
        @Override
        public long getBookerId() {
            return 2L;
        }
        @Override
        public LocalDateTime getStartDate() {
            return LocalDateTime.of(2023, 8, 8, 12, 12 );
        }
        @Override
        public LocalDateTime getEndDate() {
            return LocalDateTime.of(2023, 8, 10, 12, 12 );
        }
    };

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void findById_whenOwner_thenWithBooking() throws Exception{
        //given
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        ItemDtoWithDate itemDto = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null,  null , null);
        ItemDtoWithDate itemDto2 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null,  null , List.of());
        ItemDtoWithDate itemDto3 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, bookingDtoForOwner,  null , List.of());

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
    void findById_whenNotOwner_thenWithoutBookings() throws Exception{
        //given
        Item item = new Item(1L, "ItemName", "ItemDescription", Boolean.TRUE, 1L, null);
        ItemDtoWithDate itemDto = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null,  null , null);
        ItemDtoWithDate itemDto2 = new ItemDtoWithDate(1L, "ItemName", "ItemDescription",
                Boolean.TRUE, null,  null , List.of());

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
    void findById_whenWrongId_thenThrowsNotFoundException() throws Exception{
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
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void findByText() {
    }

    @Test
    void addComment() {
    }

    @Test
    void findAllByUser() {
    }
}