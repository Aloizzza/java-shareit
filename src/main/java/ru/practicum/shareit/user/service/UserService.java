package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getById(long id);

    List<UserDto> getAll();

    UserDto add(UserDto user);

    UserDto update(UserDto userDto, long id);

    void delete(long id);
}