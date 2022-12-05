package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto add(RequestDto requestDto, long userId);

    List<RequestDto> findAllForOwner(PageRequest pageRequest, long userId);

    List<RequestDto> findAll(PageRequest pageRequest, long userId);

    RequestDto findById(long requestId, long userId);
}
