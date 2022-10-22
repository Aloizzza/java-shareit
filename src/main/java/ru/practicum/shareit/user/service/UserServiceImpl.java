package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage inMemoryUser;

    public UserDto getById(long userId) {
        Optional<User> user = inMemoryUser.getById(userId);
        if (user.isEmpty()) {
            log.error("пользователь c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        return UserMapper.toUserDto(user.get());
    }

    public List<UserDto> getAll() {
        return inMemoryUser.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(inMemoryUser.add(user));
    }

    public UserDto update(UserDto userDto, long userId) {
        Optional<User> userToUpdate = inMemoryUser.getById(userId);
        if (userToUpdate.isEmpty()) {
            log.error("пользователь c идентификатором " + userId + " не существует");
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(inMemoryUser.update(user, userId));
    }

    public void delete(long id) {
        inMemoryUser.delete(id);
    }
}