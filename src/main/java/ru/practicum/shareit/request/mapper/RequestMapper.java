package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public class RequestMapper {
    public static Request toRequest(RequestDto requestDto) {
        return new Request(requestDto.getId(), requestDto.getDescription(), null, requestDto.getCreated());
    }

    public static RequestDto toRequestDto(Request request, List<ItemDto> items) {

        return new RequestDto(request.getId(), request.getDescription(),
                request.getCreated(), items);
    }
}
