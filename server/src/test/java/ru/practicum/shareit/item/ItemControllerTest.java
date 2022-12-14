package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {
    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private RequestController requestController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemService itemService;

    private ItemDto itemDto;

    private UserDto userDto;

    private RequestDto requestDto;

    private CommentDto comment;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(0L, "name", "description", true, null,
                null, new ArrayList<>(), 0L);

        userDto = new UserDto(0L, "name", "user@email.com");

        requestDto = new RequestDto(0L, "item request description", null, new ArrayList<>());

        comment = new CommentDto(0L, "first comment", null, null);
    }

    @Test
    void createTest() {
        UserDto user = userController.save(userDto);
        ItemDto item = itemController.save(itemDto, 1L);
        assertEquals(item.getId(), itemController.getById(item.getId(), user.getId()).getId());
    }

    @Test
    void createWithRequestTest() {
        UserDto user = userController.save(userDto);
        requestController.save(requestDto, user.getId());
        itemDto.setRequestId(1L);
        userController.save(new UserDto(0L, "name", "user1@email.com"));
        ItemDto item = itemController.save(itemDto, 2L);
        assertEquals(item.toString(), itemController.getById(1L, 2L).toString());
    }

    @Test
    void createByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemController.save(itemDto, 1L));
    }

    @Test
    void createWithWrongItemRequest() {
        itemDto.setRequestId(10L);
        userController.save(userDto);
        assertThrows(NotFoundException.class, () -> itemController.save(itemDto, 1L));
    }

    @Test
    void getAllUserItems() {
        UserDto user = userController.save(userDto);
        ItemDto item = itemController.save(itemDto, 1L);
        assertEquals(1, itemController.getAll(user.getId()).size());
    }

    @Test
    void updateTest() {
        userController.save(userDto);
        itemController.save(itemDto, 1L);
        ItemDto item = new ItemDto(0L, "new name", "updateDescription", false, null,
                null, new ArrayList<>(), 0L);
        itemController.update(item, 1L, 1L);
        assertEquals(item.getDescription(), itemController.getById(1L, 1L).getDescription());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(NotFoundException.class, () -> itemController.update(itemDto, 1L, 1L));
    }

    @Test
    void updateByWrongUserTest() {
        userController.save(userDto);
        itemController.save(itemDto, 1L);
        itemDto.setName("new name");
        assertThrows(NotFoundException.class, () -> itemController.update(itemDto, 1L, 10L));
    }

    @Test
    void searchTest() {
        userController.save(userDto);
        itemController.save(itemDto, 1L);
        assertEquals(1, itemController.search("Desc", 1L).size());
    }

    @Test
    void searchEmptyTextTest() {
        userController.save(userDto);
        itemController.save(itemDto, 1L);
        assertEquals(new ArrayList<ItemDto>(), itemController.search("", 1L));
    }

    @Test
    void createCommentTest() {
        Comment newComment = new Comment(0L, "??????????-???? ??????????????", ItemMapper.toItem(itemDto, null),
                UserMapper.toUser(userDto), LocalDateTime.now());
        CommentDto newCommentDto = CommentMapper.toCommentDto(newComment, UserMapper.toUser(userDto));
        UserDto user = userController.save(userDto);
        ItemDto item = itemController.save(itemDto, 1L);
        ItemDto.BookingForItemDto lastBooking = item.getLastBooking();
        ItemDto.BookingForItemDto nextBooking = item.getNextBooking();
        ItemDto.BookingForItemDto booking = new ItemDto.BookingForItemDto(0L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0), user.getId());
        booking.setId(1L);
        UserDto user2 = userController.save(new UserDto(0L, "name", "email2@email.com"));
        bookingController.save(new BookingDto(0L, 1L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0), WAITING,
                itemDto, user2), user2.getId());
        bookingController.approve(1L, true, 1L);
        Comment comment1 = CommentMapper.toComment(comment, ItemMapper.toItem(itemDto, null), UserMapper.toUser(userDto));
        User user1 = comment1.getAuthor();
        Item item1 = comment1.getItem();
        Booking booking1 = item1.getLastBooking();
        Booking booking2 = item1.getNextBooking();
        List<CommentDto> comments = item1.getComments();
        comment1.setCreated(LocalDateTime.now());

        assertThrows(BadRequestException.class, () -> itemController.createComment(comment, item.getId(), user2.getId()));
    }

    @Test
    void createEmptyComment() {
        comment.setText(" ");
        assertThrows(BadRequestException.class, () -> itemController.createComment(comment, 1L, 1L));
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemController.createComment(comment, 1L, 1L));
    }

    @Test
    void createCommentToWrongItem() {
        userController.save(userDto);
        assertThrows(NotFoundException.class, () -> itemController.createComment(comment, 1L, 1L));
        itemController.save(itemDto, 1L);
        assertThrows(NotFoundException.class, () -> itemController.createComment(comment, 3L, 1L));

    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(NotFoundException.class, () -> itemController.getAll(1L));
    }

    @Test
    void findItemsByRequestId() {
        UserDto user = userController.save(userDto);
        RequestDto request = requestController.save(requestDto, user.getId());
        itemDto.setRequestId(request.getId());
        itemController.save(itemDto, user.getId());

        assertEquals(1, itemService.findItemsByRequestId(request.getId()).size());
    }
}