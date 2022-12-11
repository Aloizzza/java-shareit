package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;

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
            throw new BadRequestException("время бронирования указано не корректно");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("вещь не доступна для бронирования");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(long userId, long bookingId, boolean status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("указанное бронирование не существует"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("подтверждение бронирования может быть выполнено только владельцем вещи");
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new BadRequestException("вы уже подтвердили бронирование");
        }
        booking.setStatus(status ? APPROVED : REJECTED);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));
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
    public List<BookingDto> findAllForBooker(PageRequest pageRequest, long bookerId, String state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + bookerId + " не существует"));

        return findBookings(false, state, bookerId, pageRequest)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllForOwner(PageRequest pageRequest, long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + ownerId + " не существует"));

        if (itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId).isEmpty()) {
            throw new BadRequestException("у вас нет вещей");
        }

        return findBookings(true, state, ownerId, pageRequest)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<Booking> findBookings(boolean isOwner, String state, long id, PageRequest pageRequest) {
        List<Booking> bookings;

        switch (state) {

            case "ALL":
                if (isOwner) {
                    bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(id, pageRequest);
                } else {
                    bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(id, pageRequest);
                }
                return bookings;
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;

                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }

                if (isOwner) {
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(id, status);
                } else {
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(id, status);
                }
                break;

            case "PAST":
                if (isOwner) {
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(id, LocalDateTime.now());
                } else {
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(id, LocalDateTime.now());
                }
                break;

            case "FUTURE":
                if (isOwner) {
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now());
                } else {
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now());
                }
                break;

            case "CURRENT":
                if (isOwner) {
                    bookings = bookingRepository.findCurrentOwnerBookings(id, LocalDateTime.now());
                } else {
                    bookings = bookingRepository.findCurrentBookerBookings(id, LocalDateTime.now());
                }
                break;

            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }
}