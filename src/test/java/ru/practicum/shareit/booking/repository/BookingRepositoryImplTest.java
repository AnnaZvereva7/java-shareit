package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositiry.ItemRepository;
import ru.practicum.shareit.users.UserRepository;
import ru.practicum.shareit.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({"/schemaTest.sql", "/import_tables.sql"})
class BookingRepositoryImplTest {
    @Autowired
    BookingRepository repository;
    @Autowired
    BookingRepositoryImpl repositoryImpl;
    LocalDateTime now = LocalDateTime.of(2023, 8,10,12,0);

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = repositoryImpl.findAllByBookerId(4L, 0,20) ;
        assertEquals(0, bookings.size());

        bookings = repositoryImpl.findAllByBookerId(1L, 0,20) ;
        assertEquals(3, bookings.size());
        assertEquals(3L, bookings.get(0).getId());
        assertEquals(1L, bookings.get(1).getId());
        assertEquals(2L, bookings.get(2).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllByBookerIdAndStatus() {
        repository.changeStatus(1L, "APPROVED");
   repository.changeStatus(2L, "REJECTED");
        List<Booking> bookings = repositoryImpl.findAllByBookerIdAndStatus(1L, BookingStatus.APPROVED,0,20) ;
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());

        bookings = repositoryImpl.findAllByBookerIdAndStatus(1L, BookingStatus.REJECTED,0,20) ;
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartDateAfter() {
        List<Booking> bookings =repositoryImpl.findAllByBookerIdAndStartDateAfter(1L, now, 0, 20 );
        assertEquals(1, bookings.size());
        assertEquals(3L, bookings.get(0).getId());

        bookings =repositoryImpl.findAllByBookerIdAndStartDateAfter(1L, now.plusDays(4), 0, 20 );
        assertEquals(0, bookings.size());
    }

    @Test
    void findAllByBookerIdAndEndDateBefore() {
        List<Booking> bookings =repositoryImpl.findAllByBookerIdAndEndDateBefore(1L, now, 0, 20 );
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());

      bookings =repositoryImpl.findAllByBookerIdAndEndDateBefore(1L, now.plusDays(1), 1, 20 );
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndEndDateAfterAndStartDateBefore() {
        List<Booking> bookings =repositoryImpl.findAllByBookerIdAndEndDateAfterAndStartDateBefore(1L, now, 0, 20 );
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void findAllByOwnerId() {
        List<Booking> bookings = repositoryImpl.findAllByOwnerId(2L, 0, 20);
        assertEquals(3, bookings.size());
        assertEquals(4L, bookings.get(0).getId());
        assertEquals(1L, bookings.get(1).getId());
        assertEquals(2L, bookings.get(2).getId());
        bookings = repositoryImpl.findAllByOwnerId(2L, 2, 20);
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
    }

    @Test
    void findPastByOwnerId() {
        List<Booking> bookings = repositoryImpl.findPastByOwnerId(2L, now,0, 20);
        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByOwnerIdAndStatus() {
        repository.changeStatus(1L, "APPROVED");
        repository.changeStatus(3L, "REJECTED");
        List<Booking> bookings = repositoryImpl.findByOwnerIdAndStatus(2L, BookingStatus.APPROVED ,0, 20);
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());

        bookings = repositoryImpl.findByOwnerIdAndStatus(2L, BookingStatus.REJECTED ,0, 20);
        assertEquals(0, bookings.size());

        bookings = repositoryImpl.findByOwnerIdAndStatus(3L, BookingStatus.REJECTED ,0, 20);
        assertEquals(1, bookings.size());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findCurrentByOwnerId() {
        List<Booking> bookings = repositoryImpl.findCurrentByOwnerId(2L, now ,0, 20);
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void findFutureByOwnerId() {
        List<Booking> bookings = repositoryImpl.findFutureByOwnerId(2L, now ,0, 20);
        assertEquals(1, bookings.size());
        assertEquals(4L, bookings.get(0).getId());
    }
}