package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating item {}, itemId={}", itemDto, userId);
        return itemClient.post(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto, @PathVariable long id,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Updating item{}, itemId={}", itemDto, userId);
        return itemClient.update(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Getting item, itemId={}, ownerId={}", id, ownerId);
        return itemClient.getById(id, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Getting all items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(required = false) String text,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Search items, userId={}, text={}, from={}, size={}", userId, text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@PathVariable(name = "id") long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody CommentDto text) {
        log.info("Adding a comment, itemId={}, userId={}, text={}", itemId, userId, text);
        return itemClient.addComment(itemId, userId, text);
    }
}