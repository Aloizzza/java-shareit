package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private UserDto userDto;

    private UserDto userDto1;

    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(0L, "name", "description", true, null, null,
                new ArrayList<>(), 0L);

        userDto = new UserDto(0L, "name", "user@email.com");

        userDto1 = new UserDto(0L, "name", "user1@email.com");

        bookingDto = new BookingDto(0L, 1L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0), WAITING,
                itemDto, userDto);
    }

    @Test
    void shouldCreateTest() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        assertEquals(1L, bookingController.getById(booking.getId(), user1.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        userController.save(userDto);
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 1L));
    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 1L));
    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.save(userDto);
        itemDto.setAvailable(false);
        itemController.save(itemDto, user.getId());
        userController.save(userDto1);
        assertThrows(BadRequestException.class, () -> bookingController.save(bookingDto, 2L));
    }

    @Test
    void createWithWrongEndDate() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        bookingDto.setEnd(LocalDateTime.of(2022, 9, 24, 12, 30));
        assertThrows(BadRequestException.class, () -> bookingController.save(bookingDto, user1.getId()));
    }

    @Test
    void approveTest() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        bookingDto = new BookingDto(0L, 1L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0), BookingStatus.WAITING,
                itemDto, userDto1);
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        assertEquals(WAITING, bookingController.getById(booking.getId(), user1.getId()).getStatus());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(APPROVED, bookingController.getById(booking.getId(), user1.getId()).getStatus());
    }

    @Test
    void approveToWrongBookingTest() {
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, true, 1L));
    }

    @Test
    void approveByWrongUserTest() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, true, 2L));
    }

    @Test
    void approveToBookingWithWrongStatus() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        bookingController.save(bookingDto, user1.getId());
        bookingController.approve(1L, true, 1L);
        assertThrows(BadRequestException.class, () -> bookingController.approve(1L, true, 1L));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        assertEquals(1, bookingController.getAllForBooker(0, 10, user1.getId(), "WAITING").size());
        assertEquals(1, bookingController.getAllForBooker(0, 10, user1.getId(), "ALL").size());
        assertEquals(0, bookingController.getAllForBooker(0, 10, user1.getId(), "PAST").size());
        assertEquals(0, bookingController.getAllForBooker(0, 10, user1.getId(), "CURRENT").size());
        assertEquals(1, bookingController.getAllForBooker(0, 10, user1.getId(), "FUTURE").size());
        assertEquals(0, bookingController.getAllForBooker(0, 10, user1.getId(), "REJECTED").size());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(0, bookingController.getAllForOwner(0, 10, user.getId(), "CURRENT").size());
        assertEquals(1, bookingController.getAllForOwner(0, 10, user.getId(), "ALL").size());
        assertEquals(0, bookingController.getAllForOwner(0, 10, user.getId(), "WAITING").size());
        assertEquals(1, bookingController.getAllForOwner(0, 10, user.getId(), "FUTURE").size());
        assertEquals(0, bookingController.getAllForOwner(0, 10, user.getId(), "REJECTED").size());
        assertEquals(0, bookingController.getAllForOwner(0, 10, user.getId(), "PAST").size());
    }

    @Test
    void getAllByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getAllForBooker(0, 10, 1L, "ALL"));
        assertThrows(NotFoundException.class, () -> bookingController.getAllForOwner(0, 10, 1L, "ALL"));
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        UserDto user = userController.save(userDto);
        itemController.save(itemDto, user.getId());
        UserDto user1 = userController.save(userDto1);
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 10L));
    }
}