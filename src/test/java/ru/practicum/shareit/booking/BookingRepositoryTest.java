package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;

    private Item item;

    private User user2;

    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(0L, "name", "user@email.com");

        item = new Item(0L, "name", "description", true, user, null);

        user2 = new User(0L, "name2", "email2@email.com");

        booking = new Booking(LocalDateTime.now(),
                LocalDateTime.of(2023, 11, 10, 13, 0), WAITING);
        booking.setBooker(user2);
        booking.setItem(item);
    }

    @Test
    void findCurrentOwnerBookings() {
        User userSaved = userRepository.save(user);
        itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findCurrentOwnerBookings(userSaved.getId(), LocalDateTime.now()).size(),
                equalTo(1));
    }

    @Test
    void findCurrentBookerBookings() {
        User userSaved = userRepository.save(user);
        itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findCurrentBookerBookings(userSaved2.getId(), LocalDateTime.now()).size(),
                equalTo(1));
    }

    @Test
    void findPastOwnerBookings() {
        User userSaved = userRepository.save(user);
        Item itemSaved = itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findPastOwnerBookings(itemSaved.getId(), userSaved.getId(),
                        LocalDateTime.now()).size(),
                equalTo(0));
    }

    @Test
    void findFutureOwnerBookings() {
        User userSaved = userRepository.save(user);
        item.setOwner(userSaved);
        Item itemSaved = itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        booking.setStart(LocalDateTime.of(2023, 11, 10, 13, 0));
        bookingRepository.save(booking);

        assertThat(bookingRepository.findFutureOwnerBookings(itemSaved.getId(), userSaved.getId(),
                        LocalDateTime.now()).size(),
                equalTo(1));
    }
}