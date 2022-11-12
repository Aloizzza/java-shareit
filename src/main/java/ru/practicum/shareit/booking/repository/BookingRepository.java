package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemIdAndStartBefore(long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfter(long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndItemOwnerIdAndStartBefore(long itemId, long ownerId, LocalDateTime now);

    List<Booking> findAllByItemIdAndItemOwnerIdAndStartAfter(long itemId, long ownerId, LocalDateTime now);

    Long countAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime now);
}