package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        ItemDto.BookingForItemDto lastBookingToAdd = null;
        ItemDto.BookingForItemDto nextBookingToAdd = null;

        if (lastBooking != null) {
            lastBookingToAdd = new ItemDto.BookingForItemDto(
                    lastBooking.getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd(),
                    lastBooking.getBooker().getId()
            );
        }

        if (nextBooking != null) {
            nextBookingToAdd = new ItemDto.BookingForItemDto(
                    nextBooking.getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd(),
                    nextBooking.getBooker().getId()
            );
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingToAdd,
                nextBookingToAdd,
                comments
        );
    }

    public static Item toItem(ItemDto item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null
        );
    }
}