package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public List<RequestDto> findAllForOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findAllForOwner(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestParam(required = false, defaultValue = "0") int from,
                                    @RequestParam(required = false, defaultValue = "20") int size,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findAll(from, size, userId);
    }

    @GetMapping("/{id}")
    public RequestDto findById(@PathVariable("id") long requestId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findById(requestId, userId);
    }
}