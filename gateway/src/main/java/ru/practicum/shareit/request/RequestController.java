package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GatewayRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody GatewayRequestDto itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.post(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all requests for owner, userId={}", userId);
        return requestClient.getAllOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                          @Positive @RequestParam(required = false, defaultValue = "20") int size,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all requests, from={}, size={}, userId={}", from, size, userId);
        return requestClient.getAll(from, size, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") long requestId,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting request, requestId={}, userId={}", requestId, userId);
        return requestClient.getById(requestId, userId);
    }
}