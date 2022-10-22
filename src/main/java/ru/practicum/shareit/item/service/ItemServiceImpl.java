package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.InMemoryItemStorage;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage inMemoryItem;
    private final InMemoryUserStorage inMemoryUser;

    public ItemDto getById(long itemId) {
        Optional<Item> item = inMemoryItem.getById(itemId);
        if (item.isEmpty()) {
            log.error("вещь c идентификатором " + itemId + " не существует");
            throw new NotFoundException("вещь c идентификатором " + itemId + " не существует");
        }
        return ItemMapper.toItemDto(item.get());
    }

    public List<ItemDto> getAll(long userId) {
        Optional<User> user = inMemoryUser.getById(userId);
        if (user.isEmpty()) {
            log.error("пользователь c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        return inMemoryItem.getAll()
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
        Optional<User> user = inMemoryUser.getById(userId);
        if (user.isEmpty()) {
            log.error("пользователь c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        item.setOwner(user.get());
        return ItemMapper.toItemDto(inMemoryItem.create(item));
    }

    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        Optional<User> user = inMemoryUser.getById(userId);
        if (user.isEmpty()) {
            log.error("пользователь c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        if (inMemoryItem.getById(itemId).get().getOwner().getId() != userId) {
            log.error("неверный пользователь");
            throw new NotFoundException("неверный пользователь");
        }
        return ItemMapper.toItemDto(inMemoryItem.update(item, itemId));
    }
}