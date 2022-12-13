package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("запрошен пользователь с id {}", id);

        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("запрошен список всех пользователей");

        return userService.getAll();
    }

    @PostMapping
    public UserDto save(@RequestBody UserDto userDto) {
        log.info("в базу добавлен пользователь: {}", userDto);

        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long id) {
        log.info("пользователь с id {} отредактировал свой профиль: {}", id, userDto);

        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("пользователь с id {} удален", id);

        userService.delete(id);
    }
}