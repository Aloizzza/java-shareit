package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto save(@Valid @RequestBody RequestDto itemRequestDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.add(itemRequestDto, userId);
    }

    @GetMapping
    public List<RequestDto> findAllForOwner(@RequestParam(required = false, defaultValue = "0") int from,
                                            @RequestParam(required = false, defaultValue = "20") int size,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("параметры пагинации не могут быть отрицательными или равны нулю");
        }

        return requestService.findAllForOwner(PageRequest.of(from / size, size), userId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestParam(required = false, defaultValue = "0") int from,
                                    @RequestParam(required = false, defaultValue = "20") int size,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("параметры пагинации не могут быть отрицательными или равны нулю");
        }

        return requestService.findAll(PageRequest.of(from / size, size), userId);
    }

    @GetMapping("/{id}")
    public RequestDto findById(@PathVariable("id") long requestId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findById(requestId, userId);
    }
}