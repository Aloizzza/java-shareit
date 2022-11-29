package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, long userId);

    BookingDto approve(long userId, long bookingId, boolean status);

    BookingDto getById(long bookingId, long userId);

    List<BookingDto> findAllForBooker(int from, int size, long bookerId, String state);

    List<BookingDto> findAllForOwner(int from, int size, long ownerId, String state);
}
