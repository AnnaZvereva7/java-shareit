package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BookingRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;


    public List<Booking> findAllByBookerId(long bookerId, int from, int size) {
        return entityManager
                .createQuery("SELECT b from Booking b JOIN FETCH b.booker u where u.id=:userId ORDER BY b.startDate DESC",
                        Booking.class)
                .setParameter("userId", bookerId)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, int from, int size) {
        return entityManager
                .createQuery("SELECT b from Booking b JOIN FETCH b.booker u " +
                        "where u.id=:userId and b.status=:status ORDER BY b.startDate DESC", Booking.class)
                .setParameter("userId", bookerId)
                .setParameter("status", status)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }


    public List<Booking> findAllByBookerIdAndStartDateAfter(long bookerId, LocalDateTime now, int from, int size) {
        return entityManager
                .createQuery("SELECT b from Booking b JOIN FETCH b.booker u " +
                        "where u.id=:userId and b.startDate>:now ORDER BY b.startDate DESC", Booking.class)
                .setParameter("userId", bookerId)
                .setParameter("now", now)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findAllByBookerIdAndEndDateBefore(long bookerId, LocalDateTime now, int from, int size) {
        return entityManager
                .createQuery("SELECT b from Booking b JOIN FETCH b.booker u " +
                        "where u.id=:userId and b.endDate<:now ORDER BY b.startDate DESC", Booking.class)
                .setParameter("userId", bookerId)
                .setParameter("now", now)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findAllByBookerIdAndEndDateAfterAndStartDateBefore(long bookerId, LocalDateTime now, int from, int size) {
        return entityManager
                .createQuery("SELECT b from Booking b JOIN FETCH b.booker u " +
                                "where u.id=:userId and b.endDate>:now and b.startDate<:now ORDER BY b.startDate DESC",
                        Booking.class)
                .setParameter("userId", bookerId)
                .setParameter("now", now)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findAllByOwnerId(long ownerId, int from, int size) {
        return entityManager
                .createQuery("select b from Booking b join fetch b.item i where i.ownerId=:ownerId ORDER BY b.startDate DESC",
                        Booking.class)
                .setParameter("ownerId", ownerId)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findPastByOwnerId(long ownerId, LocalDateTime now, int from, int size) {
        return entityManager
                .createQuery("select b from Booking b join fetch b.item i " +
                                "where i.ownerId=:ownerId and b.endDate<:now ORDER BY b.startDate DESC",
                        Booking.class)
                .setParameter("ownerId", ownerId)
                .setParameter("now", now)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findByOwnerIdAndStatus(long ownerId, BookingStatus status, int from, int size) {
        return entityManager
                .createQuery("select b from Booking b join fetch b.item i " +
                        "where i.ownerId=:ownerId and b.status=:status ORDER BY b.startDate DESC", Booking.class)
                .setParameter("ownerId", ownerId)
                .setParameter("status", status)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Booking> findCurrentByOwnerId(long ownerId, LocalDateTime now, int from, int size) {
        return entityManager
                .createQuery("select b from Booking b join fetch b.item i " +
                                "where i.ownerId=:ownerId and b.endDate>=:now and b.startDate<=:now ORDER BY b.startDate DESC",
                        Booking.class)
                .setParameter("ownerId", ownerId)
                .setParameter("now", now)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }


    public List<Booking> findFutureByOwnerId(long ownerId, LocalDateTime now, int from, int size) {
        return entityManager
                .createQuery("select b from Booking b join fetch b.item i " +
                                "where i.ownerId=:ownerId and b.startDate>:now ORDER BY b.startDate DESC",
                        Booking.class)
                .setParameter("ownerId", ownerId)
                .setParameter("now", now)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

}
