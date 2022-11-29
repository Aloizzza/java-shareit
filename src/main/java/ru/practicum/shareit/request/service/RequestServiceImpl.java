package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Override
    public RequestDto add(RequestDto itemRequestDto, long userId) {
        Request itemRequest = RequestMapper.toRequest(itemRequestDto);
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        requestRepository.save(itemRequest);
        return RequestMapper.toRequestDto(itemRequest, new ArrayList<>());
    }

    @Override
    public List<RequestDto> findAllForOwner(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));

        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map((Request request) -> RequestMapper.toRequestDto(request, new ArrayList<>()))
                .peek(itemRequestDtoOutput ->
                        itemRequestDtoOutput.setItems(
                                itemRepository.findItemsByRequestId(itemRequestDtoOutput.getId())
                                        .stream()
                                        .map((Item item) -> ItemMapper.toItemDto(item, null, null, null))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> findAll(int from, int size, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));
        if (from < 0 || size < 0) {
            throw new BadRequestException("параметры пагинации не могут быть отрицательными.");
        }
        if (size == 0 && from == 0) {
            throw new BadRequestException("параметры пагинации не могут быть равны нулю.");
        }

        Pageable pageable = Pageable.ofSize(size);

        return requestRepository.findAll(pageable)
                .stream()
                .filter(request -> !(request.getRequestor().getId() == userId))
                .map(request -> RequestMapper.toRequestDto(request, new ArrayList<>()))
                .peek(requestDto ->
                        requestDto.setItems(
                                itemRepository.findItemsByRequestId(requestDto.getId())
                                        .stream()
                                        .map((Item item) -> ItemMapper.toItemDto(item, null, null, null))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto findById(long requestId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("запрос с идентификатором " + requestId + " не существует."));

        RequestDto requestDto = RequestMapper.toRequestDto(request, new ArrayList<>());

        requestDto.setItems(itemRepository.findItemsByRequestId(requestId)
                .stream()
                .map((Item item) -> ItemMapper.toItemDto(item, null, null, null))
                .collect(Collectors.toList()));

        return requestDto;
    }
}
