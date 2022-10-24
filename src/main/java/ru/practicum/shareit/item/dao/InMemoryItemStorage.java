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
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    public Optional<Item> findById(long id) {
        if (!items.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(items.get(id));
    }

    public List<Item> findAll() {
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
        items.put(id, item);
        return items.get(id);
    }

    private long getId() {
        return id++;
    }
}