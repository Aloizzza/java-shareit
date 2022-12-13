package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} просмотрел вещь с id {}", userId, itemId);

        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} просмотрел список всех своих вещей", userId);

        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователем с id {} выполнен поисковый запрос: '{}'", userId, text);

        return itemService.search(text);
    }

    @PostMapping
    public ItemDto save(@RequestBody ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} добавил в базу вещь {}", userId, itemDto);

        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable long id,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} отредактировал вещь с id {}: {}", userId, id, itemDto);

        return itemService.update(itemDto, id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} оставил отзыв на вещь с id {}: {}", userId, itemId, commentDto);

        return itemService.createComment(commentDto, itemId, userId);
    }
}