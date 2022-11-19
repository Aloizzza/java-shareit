package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto getById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));

        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto update(UserDto userDto, long userId) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует"));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            userToUpdate.setName(name);
        }
        if (email != null) {
            userToUpdate.setEmail(email);
        }

        return UserMapper.toUserDto(userRepository.save(userToUpdate));
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }
}