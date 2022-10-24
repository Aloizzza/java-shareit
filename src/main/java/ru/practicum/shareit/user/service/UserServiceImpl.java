package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserStorage inMemoryUser;

    public UserDto getById(long userId) {
        return UserMapper.toUserDto(inMemoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует")));
    }

    public List<UserDto> getAll() {
        return inMemoryUser.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        for (User u : inMemoryUser.findAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new AlreadyExistsException("почта " + user.getEmail() + " уже занята другим пользователем");
            }
        }
        return UserMapper.toUserDto(inMemoryUser.add(user));
    }

    public UserDto update(UserDto userDto, long userId) {
        Optional<User> userToUpdate = inMemoryUser.findById(userId);
        if (userToUpdate.isEmpty()) {
            throw new NotFoundException("пользователь c идентификатором " + userId + " не существует");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(inMemoryUser.update(user, userId));
    }

    public void delete(long id) {
        inMemoryUser.delete(id);
    }
}