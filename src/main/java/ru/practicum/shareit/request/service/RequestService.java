package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto add(RequestDto requestDto, long userId);

    List<RequestDto> findAllForOwner(long userId);

    List<RequestDto> findAll(int from, int size, long userId);

    RequestDto findById(long requestId, long userId);
}
