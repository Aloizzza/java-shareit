package ru.practicum.shareit.user.dao;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Getter
public class InMemoryUserStorage {

    private HashMap<Long, User> users = new HashMap<>();
    private long uniqueId = 1;

    public Optional<User> getById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("пользователь c идентификатором " + id + " не существует");
        }
        return Optional.of(users.get(id));
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public User add(User user) {
        String email = user.getEmail();
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                throw new AlreadyExistsException("почта " + email + " уже занята другим пользователем");
            }
        }
        user.setId(getUniqueId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user, long id) {
        String email = user.getEmail();
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                throw new AlreadyExistsException("почта " + email + " уже занята другим пользователем");
            }
        }
        User userUpdate = users.get(id);
        if (user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        return userUpdate;
    }

    public void delete(long id) {
        users.remove(id);
    }

    public long getUniqueId() {
        return uniqueId++;
    }
}