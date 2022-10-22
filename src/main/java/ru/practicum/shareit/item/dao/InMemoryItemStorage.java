package ru.practicum.shareit.item.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Getter
@Setter
@RequiredArgsConstructor
public class InMemoryItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    public Optional<Item> getById(long id) {
        return Optional.of(items.get(id));
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        String textRequest = text.toLowerCase();
        List<Item> availableItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getDescription().trim().toLowerCase().contains(textRequest) ||
                    item.getName().trim().toLowerCase().contains(textRequest)) {
                if (item.getAvailable()) {
                    availableItems.add(item);
                }
            }
        }
        return availableItems;
    }

    public Item create(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item update(Item item, long id) {
        Item itemUpdate = items.get(id);
        if (item.getName() != null) {
            itemUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpdate.setAvailable(item.getAvailable());
        }
        items.put(id, itemUpdate);
        return itemUpdate;
    }

    private long getId() {
        return id++;
    }
}