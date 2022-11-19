package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                ItemMapper.toItemDto(booking.getItem(), null, null, new ArrayList<>()),
                UserMapper.toUserDto(booking.getBooker()));
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                BookingStatus.WAITING);
    }
}