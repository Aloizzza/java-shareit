package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> findById(long id);

    List<Item> findAll();

    List<Item> search(String text);

    Item create(Item item);

    Item update(Item item, long id);
}
