package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public ItemDto getById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c идентификатором " + itemId + " не существует"));

        Booking lastBooking = bookingRepository.findAllByItemIdAndItemOwnerIdAndStartBefore(item.getId(), userId, LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = bookingRepository.findAllByItemIdAndItemOwnerIdAndStartAfter(item.getId(), userId, LocalDateTime.now())
                .stream()
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);

        List<CommentDto> commentsDto = commentRepository.getAllByItemId(itemId).stream()
                .map(comment -> CommentMapper.toCommentDto(comment, comment.getAuthor()))
                .collect(Collectors.toList());

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto);
    }

    public List<ItemDto> getAll(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            Booking lastBooking = bookingRepository.findAllByItemIdAndStartBefore(item.getId(), LocalDateTime.now())
                    .stream()
                    .min(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookingRepository.findAllByItemIdAndStartAfter(item.getId(), LocalDateTime.now())
                    .stream()
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            List<CommentDto> commentsDto = commentRepository.getAllByItemId(item.getId())
                    .stream()
                    .map(comment -> CommentMapper.toCommentDto(comment, comment.getAuthor()))
                    .collect(Collectors.toList());

            itemsDto.add(ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto));
        }

        return itemsDto;
    }

    public List<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text, text);

        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            Booking lastBooking = bookingRepository.findAllByItemIdAndStartBefore(item.getId(), LocalDateTime.now())
                    .stream()
                    .min(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookingRepository.findAllByItemIdAndStartAfter(item.getId(), LocalDateTime.now())
                    .stream()
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            List<CommentDto> commentsDto = commentRepository.getAllByItemId(item.getId())
                    .stream()
                    .map(comment -> CommentMapper.toCommentDto(comment, comment.getAuthor()))
                    .collect(Collectors.toList());

            itemsDto.add(ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto));
        }

        return itemsDto;
    }

    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        item.setOwner(user.get());

        return ItemMapper.toItemDto(itemRepository.save(item), null, null, new ArrayList<>());
    }

    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c идентификатором " + itemId + " не существует"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("вещь принадлежит другому пользователю");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);

        Booking lastBooking = bookingRepository.findAllByItemIdAndStartBefore(item.getId(), LocalDateTime.now())
                .stream()
                .min(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = bookingRepository.findAllByItemIdAndStartAfter(item.getId(), LocalDateTime.now())
                .stream()
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);

        List<CommentDto> commentsDto = commentRepository.getAllByItemId(item.getId())
                .stream()
                .map(comment -> CommentMapper.toCommentDto(comment, comment.getAuthor()))
                .collect(Collectors.toList());

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentsDto);
    }

    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new BadRequest("отзыв не может быть пустым");
        }

        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        System.out.println("bookingsCount=" + bookingsCount);

        if (bookingsCount == null || bookingsCount == 0) {
            throw new BadRequest("вы еще ни разу не брали эту вещь");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c идентификатором " + itemId + " не существует"));

        User user = UserMapper.toUser(userService.getById(userId));
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment), user);
    }
}