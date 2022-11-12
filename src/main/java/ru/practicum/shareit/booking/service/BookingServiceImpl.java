package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto save(BookingDto bookingDto, long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("вещь с id " + bookingDto.getItemId() + " не найдена"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("эта вещь - ваша");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequest("время бронирования указано не корректно");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        if (!booking.getItem().getAvailable()) {
            throw new BadRequest("вещь не доступна для бронирования");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(long userId, long bookingId, boolean status) {
        userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("указанное бронирование не существует"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("подтверждение бронирования может быть выполнено только владельцем вещи");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequest("вы уже подтвердили бронирование");
        }
        booking.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("указанное бронирование не существует"));
        long id = booking.getItem().getId();
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("вещь с id " + id + " не найдена."));
        if (booking.getBooker().getId() != userId) {
            if (item.getOwner().getId() != userId) {
                throw new NotFoundException("вещь забронирована не вами");
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllForBooker(long bookerId, String state) {
        userRepository.findById(bookerId)
                .orElseThrow(()-> new NotFoundException("пользователь c идентификатором " + bookerId + " не существует"));
        return findBookings(bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId), state).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllForOwner(long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(()-> new NotFoundException("пользователь c идентификатором " + ownerId + " не существует"));
        if (itemRepository.findAllByOwnerId(ownerId).isEmpty()) {
            throw new BadRequest("у вас нет вещей");
        }
        return findBookings(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId), state).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public List<Booking> findBookings(List<Booking> bookings, String state) {
        switch (state) {
            case "ALL":
                return bookings;
            case "WAITING":
                return bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream().filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                        && booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            default:
                throw new BadRequest("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}