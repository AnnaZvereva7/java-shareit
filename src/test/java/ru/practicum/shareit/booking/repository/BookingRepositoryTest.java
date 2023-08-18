package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository repository;

    LocalDateTime now = LocalDateTime.of(2023, 8,10,12,0);

    @Test
    @Sql({"/schemaTest.sql"})
    void findById_whenEmpty() {
        Optional<Booking> booking = repository.findById(1L);
        assertThat(booking).isEmpty();
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findById_whenNotEmpty() {
        Optional<Booking> booking = repository.findById(1L);
        assertThat(booking).isPresent();
        assertEquals(1L, booking.get().getId());
        assertEquals(LocalDateTime.of(2023,8,9,12,0), booking.get().getStartDate());
        assertEquals(LocalDateTime.of(2023,8,10,15,0), booking.get().getEndDate());
        assertEquals(4L, booking.get().getItem().getId());
        assertEquals("name4", booking.get().getItem().getName());
        assertEquals(1L, booking.get().getBooker().getId());
        assertEquals("email@mail.ru", booking.get().getBooker().getEmail());
        assertEquals(BookingStatus.WAITING, booking.get().getStatus());
    }


    @Test
    @Sql({"/schemaTest.sql"})
    void findAllBookingPeriodsForItemId_whenEmpty() {
        List<BookingPeriod> periods = repository.findAllBookingPeriodsForItemId(4L, now);
        assertEquals(0, periods.size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllBookingPeriodsForItemId() {
        List<BookingPeriod> periods = repository.findAllBookingPeriodsForItemId(5L, now);
        assertEquals(0, periods.size());

        periods = repository.findAllBookingPeriodsForItemId(4L, now);
        assertEquals(2, periods.size());
        assertEquals(now.minusDays(1), periods.get(0).getStartDate());
        assertEquals(now.plusDays(2), periods.get(1).getStartDate());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void countByOwnerIdBookingId() {
        int count = repository.countByOwnerIdBookingId(2L, 3L);
        assertEquals(0, count);

        count = repository.countByOwnerIdBookingId(2L, 4L);
        assertEquals(1, count);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void countByBookerIdBookingId() {
        int count = repository.countByBookerIdBookingId(1L, 4L);
        assertEquals(0, count);

        count = repository.countByBookerIdBookingId(1L, 3L);
        assertEquals(1, count);
    }


    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void getStatusById() {
        String status = repository.getStatusById(1L);
        assertEquals("WAITING", status);

        status = repository.getStatusById(5);
        assertEquals(null, status);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void changeStatus() {
        repository.changeStatus(1L, "APPROVED");
        Optional<Booking> updatedBooking = repository.findById(1L);
        assertThat(updatedBooking).isPresent();
        assertEquals(BookingStatus.APPROVED, updatedBooking.get().getStatus());

        repository.changeStatus(1L, "REJECTED");
        updatedBooking = repository.findById(1L);
        assertEquals(BookingStatus.APPROVED, updatedBooking.get().getStatus());
    }


    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findLastBookingForItem() {

        List<BookingDtoForOwner> bookingsDto = repository.findLastBookingForItem(List.of(7L), now);
        assertEquals(0, bookingsDto.size());

        List<BookingDtoForOwner> bookings = repository.findLastBookingForItem(List.of(4L), now);
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(4L, bookings.get(0).getItemId());
        assertEquals(1L, bookings.get(0).getBookerId());
        assertEquals(now.minusDays(1), bookings.get(0).getStartDate());
        assertEquals(now.plusHours(3), bookings.get(0).getEndDate());

        bookingsDto = repository.findLastBookingForItem(List.of(4L), now.plusDays(5));
        assertEquals(1, bookingsDto.size());
        assertEquals(4L, bookingsDto.get(0).getId());
        assertEquals(4L, bookingsDto.get(0).getItemId());
        assertEquals(3L, bookingsDto.get(0).getBookerId());
        assertEquals(now.plusDays(2), bookingsDto.get(0).getStartDate());
        assertEquals(now.plusDays(3), bookingsDto.get(0).getEndDate());

        bookingsDto = repository.findLastBookingForItem(List.of(4L, 5L), now);
        assertEquals(2, bookingsDto.size());
        assertEquals(1L, bookingsDto.get(0).getId());
        assertEquals(4L, bookingsDto.get(0).getItemId());
        assertEquals(1L, bookingsDto.get(0).getBookerId());
        assertEquals(now.minusDays(1), bookingsDto.get(0).getStartDate());
        assertEquals(now.plusHours(3), bookingsDto.get(0).getEndDate());
        assertEquals(2L, bookingsDto.get(1).getId());
        assertEquals(5L, bookingsDto.get(1).getItemId());
        assertEquals(1L, bookingsDto.get(1).getBookerId());
        assertEquals(now.minusDays(3), bookingsDto.get(1).getStartDate());
        assertEquals(now.minusDays(2), bookingsDto.get(1).getEndDate());

    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findNextBookingForItems() {
        List<BookingDtoForOwner> bookingsDto = repository.findNextBookingForItems(List.of(7L), now);
        assertEquals(0, bookingsDto.size());


        bookingsDto = repository.findNextBookingForItems(List.of(4L), now);
        assertEquals(1, bookingsDto.size());
        assertEquals(4L, bookingsDto.get(0).getId());
        assertEquals(4L, bookingsDto.get(0).getItemId());
        assertEquals(3L, bookingsDto.get(0).getBookerId());
        assertEquals(now.plusDays(2), bookingsDto.get(0).getStartDate());
        assertEquals(now.plusDays(3), bookingsDto.get(0).getEndDate());

        bookingsDto = repository.findNextBookingForItems(List.of(4L), now.minusDays(3));
        assertEquals(1, bookingsDto.size());
        assertEquals(1L, bookingsDto.get(0).getId());
        assertEquals(4L, bookingsDto.get(0).getItemId());
        assertEquals(1L, bookingsDto.get(0).getBookerId());
        assertEquals(now.minusDays(1), bookingsDto.get(0).getStartDate());
        assertEquals(now.plusHours(3), bookingsDto.get(0).getEndDate());

        bookingsDto = repository.findNextBookingForItems(List.of(4L, 6L), now);
        assertEquals(2, bookingsDto.size());
        assertEquals(3L, bookingsDto.get(0).getId());
        assertEquals(6L, bookingsDto.get(0).getItemId());
        assertEquals(1L, bookingsDto.get(0).getBookerId());
        assertEquals(now.plusDays(1), bookingsDto.get(0).getStartDate());
        assertEquals(now.plusDays(2), bookingsDto.get(0).getEndDate());
        assertEquals(4L, bookingsDto.get(1).getId());
        assertEquals(4L, bookingsDto.get(1).getItemId());
        assertEquals(3L, bookingsDto.get(1).getBookerId());
        assertEquals(now.plusDays(2), bookingsDto.get(1).getStartDate());
        assertEquals(now.plusDays(3), bookingsDto.get(1).getEndDate());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void checkForComment() {
        int count = repository.checkForComment(1L, 5L, now);
        assertEquals(0, count);

        repository.changeStatus(2L, BookingStatus.APPROVED.name());
        count = repository.checkForComment(1L, 5L, now);
        assertEquals(1, count);
    }
}