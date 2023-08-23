package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.dto.BookingPeriodImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.LimitAccessException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository repository;

    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;

    private BookingServiceImpl service;
    private Clock clock;

    @BeforeEach
    public void before() {
        clock = Clock.fixed(
                Instant.parse("2023-08-10T12:00:00.00Z"),
                ZoneId.of("UTC"));
        service = new BookingServiceImpl(repository, userService, itemService, clock);
    }

    @Test
    void create_whenItemWrong() {
        LocalDateTime now = LocalDateTime.now(clock);
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenThrow(new NotFoundException(Item.class));
        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 1L);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookerIsOwner() {
        LocalDateTime now = LocalDateTime.now(clock);
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(new Item(1L, "itemName", "itemDescription", true, 2L, null));

        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 2L);
        });

        assertThat(thrown).isInstanceOf(LimitAccessException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemNotAvailable() {
        LocalDateTime now = LocalDateTime.now(clock);
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(new Item(1L, "itemName", "itemDescription", false, 2L, null));

        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 1L);
        });

        assertThat(thrown).isInstanceOf(NotAvailableException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookingPeriodCross1() {
        LocalDateTime now = LocalDateTime.now(clock);
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(new Item(1L, "itemName", "itemDescription", true, 2L, null));
        BookingPeriod period1 = new BookingPeriodImpl(now.minusDays(1), now.plusDays(1));
        BookingPeriod period2 = new BookingPeriodImpl(now.plusDays(4), now.plusDays(6));
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of(period1, period2));

        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 1L);
        });

        assertThat(thrown).isInstanceOf(NotAvailableException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookingPeriodCross2() {
        LocalDateTime now = LocalDateTime.now(clock);
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(new Item(1L, "itemName", "itemDescription", true, 2L, null));
        BookingPeriod period1 = new BookingPeriodImpl(now.plusDays(2), now.plusDays(4));
        BookingPeriod period2 = new BookingPeriodImpl(now.plusDays(6), now.plusDays(7));
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of(period1, period2));

        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 1L);
        });

        assertThat(thrown).isInstanceOf(NotAvailableException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookingPeriodCross3() {
        LocalDateTime now = LocalDateTime.now(clock);
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(new Item(1L, "itemName", "itemDescription", true, 2L, null));
        BookingPeriod period1 = new BookingPeriodImpl(now.plusDays(2), now.plusDays(4));
        BookingPeriod period2 = new BookingPeriodImpl(now.plusDays(4).plusHours(1), now.plusDays(7));
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of(period1, period2));

        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 1L);
        });

        assertThat(thrown).isInstanceOf(NotAvailableException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookingPeriodBetween() {
        LocalDateTime now = LocalDateTime.now(clock);
        Item item = new Item(1L, "itemName", "itemDescription", true, 2L, null);
        User booker = new User(1L, "userName", "email@mail.ru");
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(item);
        BookingPeriod period1 = new BookingPeriodImpl(now.plusDays(1), now.plusDays(2));
        BookingPeriod period2 = new BookingPeriodImpl(now.plusDays(6), now.plusDays(7));
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of(period1, period2));
        when(userService.findById(1L)).thenReturn(booker);

        Booking booking = new Booking(null, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        Booking expectedBooking = new Booking(1L, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        when(repository.save(booking)).thenReturn(expectedBooking);

        Booking actualBooking = service.create(bookingDto, 1L);
        verify(repository, times(1)).save(any(Booking.class));
        assertEquals(expectedBooking.getId(), actualBooking.getId());
        assertEquals(expectedBooking.getStartDate(), actualBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actualBooking.getEndDate());
        assertEquals(expectedBooking.getItem(), actualBooking.getItem());
        assertEquals(expectedBooking.getBooker(), actualBooking.getBooker());
        assertEquals(expectedBooking.getStatus(), actualBooking.getStatus());
    }

    @Test
    void create_whenBookingPeriodBefore() {
        LocalDateTime now = LocalDateTime.now(clock);
        Item item = new Item(1L, "itemName", "itemDescription", true, 2L, null);
        User booker = new User(1L, "userName", "email@mail.ru");
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(item);
        BookingPeriod period2 = new BookingPeriodImpl(now.plusDays(6), now.plusDays(7));
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of(period2));
        when(userService.findById(1L)).thenReturn(booker);

        Booking booking = new Booking(null, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        Booking expectedBooking = new Booking(1L, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        when(repository.save(booking)).thenReturn(expectedBooking);

        Booking actualBooking = service.create(bookingDto, 1L);
        verify(repository, times(1)).save(any(Booking.class));
        assertEquals(expectedBooking.getId(), actualBooking.getId());
        assertEquals(expectedBooking.getStartDate(), actualBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actualBooking.getEndDate());
        assertEquals(expectedBooking.getItem(), actualBooking.getItem());
        assertEquals(expectedBooking.getBooker(), actualBooking.getBooker());
        assertEquals(expectedBooking.getStatus(), actualBooking.getStatus());
    }

    @Test
    void create_whenBookingPeriodAfter() {
        LocalDateTime now = LocalDateTime.now(clock);
        Item item = new Item(1L, "itemName", "itemDescription", true, 2L, null);
        User booker = new User(1L, "userName", "email@mail.ru");
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(item);
        BookingPeriod period1 = new BookingPeriodImpl(now.plusDays(1), now.plusDays(2));
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of(period1));
        when(userService.findById(1L)).thenReturn(booker);

        Booking booking = new Booking(null, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        Booking expectedBooking = new Booking(1L, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        when(repository.save(booking)).thenReturn(expectedBooking);

        Booking actualBooking = service.create(bookingDto, 1L);
        verify(repository, times(1)).save(any(Booking.class));
        assertEquals(expectedBooking.getId(), actualBooking.getId());
        assertEquals(expectedBooking.getStartDate(), actualBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actualBooking.getEndDate());
        assertEquals(expectedBooking.getItem(), actualBooking.getItem());
        assertEquals(expectedBooking.getBooker(), actualBooking.getBooker());
        assertEquals(expectedBooking.getStatus(), actualBooking.getStatus());
    }

    @Test
    void create_whenFirstBooking() {
        LocalDateTime now = LocalDateTime.now(clock);
        Item item = new Item(1L, "itemName", "itemDescription", true, 2L, null);
        User booker = new User(1L, "userName", "email@mail.ru");
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(item);
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of());
        when(userService.findById(1L)).thenReturn(booker);

        Booking booking = new Booking(null, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        Booking expectedBooking = new Booking(1L, now.plusDays(3), now.plusDays(5), item, booker, BookingStatus.WAITING);
        when(repository.save(booking)).thenReturn(expectedBooking);

        Booking actualBooking = service.create(bookingDto, 1L);
        verify(repository, times(1)).save(any(Booking.class));
        assertEquals(expectedBooking.getId(), actualBooking.getId());
        assertEquals(expectedBooking.getStartDate(), actualBooking.getStartDate());
        assertEquals(expectedBooking.getEndDate(), actualBooking.getEndDate());
        assertEquals(expectedBooking.getItem(), actualBooking.getItem());
        assertEquals(expectedBooking.getBooker(), actualBooking.getBooker());
        assertEquals(expectedBooking.getStatus(), actualBooking.getStatus());
    }

    @Test
    void create_whenWrongBooker() {
        LocalDateTime now = LocalDateTime.now(clock);
        Item item = new Item(1L, "itemName", "itemDescription", true, 2L, null);
        User booker = new User(1L, "userName", "email@mail.ru");
        BookingDtoRequest bookingDto = new BookingDtoRequest(1L, now.plusDays(3), now.plusDays(5));
        when(itemService.findById(1L)).thenReturn(item);
        when(repository.findAllBookingPeriodsForItemId(1L, now)).thenReturn(List.of());

        when(userService.findById(1L)).thenThrow(new NotFoundException(User.class));

        Throwable thrown = catchThrowable(() -> {
            service.create(bookingDto, 1L);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
        verify(repository, never()).save(any(Booking.class));
    }

    @Test
    void approval_whenStatusNotWaiting() {
        String status = "APPROVED";
        when(repository.getStatusById(1L)).thenReturn(status);

        Throwable thrown = catchThrowable(() -> {
            service.approval(1L, true);
        });

        assertThat(thrown).isInstanceOf(WrongStatusException.class);
        verify(repository, never()).changeStatus(anyLong(), anyString());
    }

    @Test
    void approval_whenWrongBooking() {
        when(repository.getStatusById(1L)).thenReturn(null);

        Throwable thrown = catchThrowable(() -> {
            service.approval(1L, true);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
        verify(repository, never()).changeStatus(anyLong(), anyString());
    }

    @Test
    void approval_whenApproved() {
        String status = "WAITING";
        when(repository.getStatusById(1L)).thenReturn(status);
        doNothing().when(repository).changeStatus(1L, BookingStatus.APPROVED.name());
        when(repository.findById(1L)).thenReturn(Optional.of(new Booking()));
        Booking booking = service.approval(1L, true);
        verify(repository, times(1)).changeStatus(1L, BookingStatus.APPROVED.toString());
        verify(repository, never()).changeStatus(1L, BookingStatus.REJECTED.toString());
    }

    @Test
    void approval_whenRejected() {
        String status = "WAITING";
        when(repository.getStatusById(1L)).thenReturn(status);
        doNothing().when(repository).changeStatus(1L, BookingStatus.REJECTED.name());
        when(repository.findById(1L)).thenReturn(Optional.of(new Booking()));
        Booking booking = service.approval(1L, false);
        verify(repository, never()).changeStatus(1L, BookingStatus.APPROVED.toString());
        verify(repository, times(1)).changeStatus(1L, BookingStatus.REJECTED.toString());
    }

    @Test
    void isUserOwner_whenTrue() {
        when(repository.countByOwnerIdBookingId(1L, 1L)).thenReturn(1);
        Boolean response = service.isUserOwner(1L, 1L);
        assertEquals(true, response);
    }

    @Test
    void isUserOwner_whenFalse() {
        when(repository.countByOwnerIdBookingId(1L, 1L)).thenReturn(0);
        Boolean response = service.isUserOwner(1L, 1L);
        assertEquals(false, response);
    }

    @Test
    void isUserBooker_whenTrue() {
        when(repository.countByBookerIdBookingId(1L, 1L)).thenReturn(1);
        Boolean response = service.isUserBooker(1L, 1L);
        assertEquals(true, response);
    }

    @Test
    void isUserBooker_whenFalse() {
        when(repository.countByBookerIdBookingId(1L, 1L)).thenReturn(0);
        Boolean response = service.isUserBooker(1L, 1L);
        assertEquals(false, response);
    }

    @Test
    void findById() {
        LocalDateTime now = LocalDateTime.now(clock);
        Booking booking = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), new User(), BookingStatus.WAITING);
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        Booking actualBooking = service.findById(1L);
        assertEquals(actualBooking, booking);
    }

    @Test
    void findById_whenEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            service.findById(1L);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }


    @Test
    void findAllByOwner_whenWrongOwner() {
        when(userService.findById(anyLong())).thenThrow(new NotFoundException(User.class));

        Throwable thrown = catchThrowable(() -> {
            service.findAllByOwner(1L, State.ALL, 0, 20);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByOwner_whenAll() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), new User(), BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), new User(), BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(owner);
        when(repository.findAllByOwnerId(1L, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByOwner(1L, State.ALL, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByOwner_whenPast() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.minusDays(2), now.minusDays(1), new Item(), new User(), BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.minusDays(4), now.minusDays(3), new Item(), new User(), BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(owner);
        when(repository.findPastByOwnerId(1L, now, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByOwner(1L, State.PAST, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByOwner_whenWaiting() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), new User(), BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), new User(), BookingStatus.WAITING);
        when(userService.findById(anyLong())).thenReturn(owner);
        when(repository.findByOwnerIdAndStatus(1L, BookingStatus.WAITING, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByOwner(1L, State.WAITING, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByOwner_whenRejected() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), new User(), BookingStatus.REJECTED);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), new User(), BookingStatus.REJECTED);
        when(userService.findById(anyLong())).thenReturn(owner);
        when(repository.findByOwnerIdAndStatus(1L, BookingStatus.REJECTED, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByOwner(1L, State.REJECTED, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByOwner_whenCurrent() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.minusDays(1), now.plusDays(2), new Item(), new User(), BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.minusDays(2), now.plusDays(3), new Item(), new User(), BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(owner);
        when(repository.findCurrentByOwnerId(1L, now, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByOwner(1L, State.CURRENT, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByOwner_whenFuture() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusHours(1), now.plusDays(2), new Item(), new User(), BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), new User(), BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(owner);
        when(repository.findFutureByOwnerId(1L, now, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByOwner(1L, State.FUTURE, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByOwner_whenDefault() {
        LocalDateTime now = LocalDateTime.now(clock);
        User owner = new User(1L, "name", "email@mail.ru");
        when(userService.findById(anyLong())).thenReturn(owner);

        Throwable thrown = catchThrowable(() -> {
            service.findAllByOwner(1L, null, 0, 20);
        });

        assertThat(thrown).isInstanceOf(RuntimeException.class);
    }

    @Test
    void findAllByBooker_whenWrongUser() {
        when(userService.findById(anyLong())).thenThrow(new NotFoundException(User.class));

        Throwable thrown = catchThrowable(() -> {
            service.findAllByBooker(1L, State.ALL, 0, 20);
        });

        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByBooker_whenAll() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), booker, BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(booker);
        when(repository.findAllByBookerId(1L, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByBooker(1L, State.ALL, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByBooker_whenPast() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.minusDays(2), now.minusDays(1), new Item(), booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.minusDays(4), now.minusDays(3), new Item(), booker, BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(booker);
        when(repository.findAllByBookerIdAndEndDateBefore(1L, now, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByBooker(1L, State.PAST, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByBooker_whenWaiting() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), booker, BookingStatus.WAITING);
        when(userService.findById(anyLong())).thenReturn(booker);
        when(repository.findAllByBookerIdAndStatus(1L, BookingStatus.WAITING, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByBooker(1L, State.WAITING, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByBooker_whenRejected() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusDays(1), now.plusDays(2), new Item(), booker, BookingStatus.REJECTED);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), booker, BookingStatus.REJECTED);
        when(userService.findById(anyLong())).thenReturn(booker);
        when(repository.findAllByBookerIdAndStatus(1L, BookingStatus.REJECTED, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByBooker(1L, State.REJECTED, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByBooker_whenCurrent() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.minusDays(1), now.plusDays(2), new Item(), booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.minusDays(2), now.plusDays(3), new Item(), booker, BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(booker);
        when(repository.findAllByBookerIdAndEndDateAfterAndStartDateBefore(1L, now, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByBooker(1L, State.CURRENT, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByBooker_whenFuture() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        Booking booking1 = new Booking(1L, now.plusHours(1), now.plusDays(2), new Item(), booker, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, now.plusDays(2), now.plusDays(3), new Item(), booker, BookingStatus.APPROVED);
        when(userService.findById(anyLong())).thenReturn(booker);
        when(repository.findAllByBookerIdAndStartDateAfter(1L, now, 0, 20)).thenReturn(List.of(booking1, booking2));

        List<Booking> actualBookings = service.findAllByBooker(1L, State.FUTURE, 0, 20);

        assertEquals(2, actualBookings.size());
        assertEquals(booking1, actualBookings.get(0));
        assertEquals(booking2, actualBookings.get(1));
    }

    @Test
    void findAllByBooker_whenDefault() {
        LocalDateTime now = LocalDateTime.now(clock);
        User booker = new User(1L, "name", "email@mail.ru");
        when(userService.findById(anyLong())).thenReturn(booker);

        Throwable thrown = catchThrowable(() -> {
            service.findAllByBooker(1L, null, 0, 20);
        });

        assertThat(thrown).isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkForComment_whenOk() {
        LocalDateTime now = LocalDateTime.now(clock);
        when(repository.checkForComment(1L, 1L, now)).thenReturn(1);
        assertEquals(true, service.checkForComment(1L, 1L));
    }

    @Test
    void checkForComment_whenNotAvailable() {
        LocalDateTime now = LocalDateTime.now(clock);
        when(repository.checkForComment(1L, 2L, now)).thenThrow(new NotAvailableException("for comment"));

        Throwable thrown = catchThrowable(() -> {
            service.checkForComment(1L, 2L);
        });

        assertThat(thrown).isInstanceOf(NotAvailableException.class);
    }

}