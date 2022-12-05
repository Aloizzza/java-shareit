package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private UserController userController;

    private UserDto user;

    @BeforeEach
    void beforeEach() {
        user = new UserDto(0L, "name", "user@email.com");
    }

    @Test
    void createTest() {
        UserDto userDto = userController.save(user);
        assertEquals(userDto.getId(), userController.getById(userDto.getId()).getId());
    }

    @Test
    void updateTest() {
        UserDto user1 = userController.save(user);
        user.setName("update name");
        user.setEmail("update@email.com");
        UserDto userDto = userController.update(user, user1.getId());
        userController.update(userDto, 1L);
        assertEquals(userDto.getEmail(), userController.getById(1L).getEmail());
    }

    @Test
    void updateByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> userController.update(user, 1L));
    }

    @Test
    void deleteTest() {
        UserDto userDto = userController.save(user);
        assertEquals(1, userController.getAll().size());
        userController.delete(userDto.getId());
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(NotFoundException.class, () -> userController.getById(1L));
    }
}