package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage inMemoryItem;
    private final UserStorage inMemoryUser;

    public ItemDto getById(long itemId) {
        return ItemMapper.toItemDto(inMemoryItem.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c идентификатором " + itemId + " не существует")));
    }

    public List<ItemDto> getAll(long userId) {
        Optional<User> user = inMemoryUser.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        return inMemoryItem.findAll()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text) {
        return inMemoryItem.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> user = inMemoryUser.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        item.setOwner(user.get());
        return ItemMapper.toItemDto(inMemoryItem.create(item));
    }

    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = inMemoryItem.findById(itemId)
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
        inMemoryItem.update(item, itemId);
        return ItemMapper.toItemDto(item);
    }
}