package ru.practicum.shareit.user.dao;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Getter
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> users = new HashMap<>();
    private long uniqueId = 1;

    public Optional<User> findById(long id) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User add(User user) {
        user.setId(getId());
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

    private long getId() {
        return uniqueId++;
    }
}