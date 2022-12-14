package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, long userId);

    BookingDto approve(long userId, long bookingId, boolean status);

    BookingDto getById(long bookingId, long userId);

    List<BookingDto> findAllForBooker(PageRequest pageRequest, long bookerId, String state);

    List<BookingDto> findAllForOwner(PageRequest pageRequest, long ownerId, String state);
}
