package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(long id);

    List<User> findAll();

    User add(User user);

    User update(User user, long id);
}
