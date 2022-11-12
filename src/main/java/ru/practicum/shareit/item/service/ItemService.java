package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getById(long itemId, long userId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> search(String text);

    ItemDto create(ItemDto item, long userId);

    ItemDto update(ItemDto itemDto, long id, long userId);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}