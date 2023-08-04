package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForOwner;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.booking.model.Booking;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findById(long id);

    @Query(value = "select booking.start_date as startDate, booking.end_date as endDate " +
            "from booking inner join items on booking.item_id=items.id where items.id = ?1 " +
            "and booking.end_date<?2 order by booking.start_date", nativeQuery = true)
    List<BookingPeriod> findAllBookingPeriodsForItemId(long itemId, LocalDateTime now);

    @Query(value = "select count(booking.id) from booking inner join items on booking.item_id=items.id " +
            "where booking.id=?2 and items.owner_id=?1 ", nativeQuery = true)
    int countByOwnerIdBookingId(long ownerId, long bookingId);

    @Query(value = "select count(id) from booking where id=?2 and booker_id=?1", nativeQuery = true)
    int countByBookerIdBookingId(long bookerId, long bookingId);

    @Query(value = "select status from booking where id=?1 ", nativeQuery = true)
    String getStatusById(long bookingId);

    @Modifying
    @Transactional
    @Query(value = "update booking set status=?2 where id=?1 and status='WAITING'", nativeQuery = true)
    void changeStatus(long bookingId, String status);

    List<Booking> findAllByBookerIdOrderByStartDateDesc(long bookerId);

    @Query(value = "select * from booking inner join items on booking.item_id=items.id where items.owner_id=?1 " +
            "order by booking.start_date desc", nativeQuery = true)
    List<Booking> findAllByOwnerId(long ownerId);

    @Query(value = "select booking.id, booking.booker_id as bookerId, booking.start_date as startDate, " +
            "booking.end_date as endDate from booking inner join items on booking.item_id=items.id " +
            "where items.id = ?1 and booking.start_date<=?2 " +
            "and booking.status in ('APPROVED', 'WAITING', 'CANCELED') " +
            "order by booking.start_date desc limit 1", nativeQuery = true)
    BookingForOwner findLastBookingPeriodForItem(long itemId, LocalDateTime now);

    @Query(value = "select booking.id, booking.booker_id as bookerId, booking.start_date as startDate, " +
            "booking.end_date as endDate from booking inner join items on booking.item_id=items.id " +
            "where items.id = ?1 and booking.start_date>?2 and booking.status in ('APPROVED', 'WAITING') " +
            "order by booking.start_date limit 1", nativeQuery = true)
    BookingForOwner findNextBookingPeriodForItem(long itemId, LocalDateTime now);

    @Query(value = "select count(booking.id) from items inner join booking on items.id=booking.item_id " +
            "where booking.booker_id=?1 and items.id=?2 and booking.status in ('APPROVED', 'CANCELED') " +
            "and booking.end_date<?3", nativeQuery = true)
    int checkForComment(long userId, long itemId, LocalDateTime now);
}
