package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(@RequestBody BookingDto bookingDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable long bookingId, @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllForBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findAllForBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") long owner,
                                                 @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findAllForOwner(owner, state);
    }
}