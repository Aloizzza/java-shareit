package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, long userId);

    BookingDto approve(long userId, long bookingId, boolean status);

    BookingDto getById(long bookingId, long userId);

    List<BookingDto> findAllForBooker(long bookerId, String state);

    List<BookingDto> findAllForOwner(long ownerId, String state);
}
