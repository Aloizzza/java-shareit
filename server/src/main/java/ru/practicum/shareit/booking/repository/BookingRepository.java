package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentOwnerBookings(long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentBookerBookings(long bookerId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.end < ?3 order by b.start desc")
    List<Booking> findPastOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start > ?3 " +
            "order by b.start desc")
    List<Booking> findFutureOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    Long countAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime now);
}