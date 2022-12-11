package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestControllerTest {
    @Autowired
    private RequestController requestController;

    @Autowired
    private UserController userController;

    private RequestDto itemRequestDto;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = new RequestDto(0L, "item request description", null, new ArrayList<>());

        userDto = new UserDto(0L, "name", "user@email.com");
    }

    @Test
    void createTest() {
        UserDto user = userController.save(userDto);
        RequestDto itemRequest = requestController.save(itemRequestDto, user.getId());
        List<ItemDto> items = itemRequest.getItems();
        Request request = RequestMapper.toRequest(itemRequest);
        User user1 = request.getRequestor();

        assertEquals(1L, requestController.findById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void findWithItemTest() {
        UserDto user = userController.save(userDto);
        UserDto user2 = userController.save(new UserDto(0L, "name", "user2@email.com"));
        RequestDto itemRequest = requestController.save(itemRequestDto, user.getId());
        Item item = new Item(0L, "item", "desc", true, UserMapper.toUser(user2),
                RequestMapper.toRequest(itemRequest));

        assertEquals(1, requestController.findAll(0, 20, user2.getId()).size());
        assertEquals(1, requestController.findAllForOwner(0, 20, user.getId()).size());
    }

    @Test
    void findWithBadPagination() {
        UserDto user = userController.save(userDto);
        UserDto user2 = userController.save(new UserDto(0L, "name", "user2@email.com"));
        RequestDto itemRequest = requestController.save(itemRequestDto, user.getId());
        Item item = new Item(0L, "item", "desc", true, UserMapper.toUser(user2),
                RequestMapper.toRequest(itemRequest));

        assertThrows(BadRequestException.class, () -> requestController.findAllForOwner(-1, 0, user.getId()).size());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> requestController.save(itemRequestDto, 1L));
    }

    @Test
    void getAllByOwnerTest() {
        UserDto user = userController.save(userDto);
        requestController.save(itemRequestDto, user.getId());
        assertEquals(1, requestController.findAllForOwner(0, 20, user.getId()).size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NotFoundException.class, () -> requestController.findAll(0, 20, 1L));
    }

    @Test
    void getAll() {
        UserDto user = userController.save(userDto);
        requestController.save(itemRequestDto, user.getId());
        assertEquals(0, requestController.findAll(0, 10, user.getId()).size());

        UserDto user2 = userController.save(new UserDto(0L, "name", "user1@email.com"));
        assertEquals(1, requestController.findAll(0, 10, user2.getId()).size());
    }

    @Test
    void getAllByWrongUser() {
        assertThrows(NotFoundException.class, () -> requestController.findAll(0, 10, 1L));
    }

    @Test
    void getAllWithWrongFrom() {
        userController.save(userDto);
        assertThrows(BadRequestException.class, () -> requestController.findAll(-1, 10, 1L));
    }
}