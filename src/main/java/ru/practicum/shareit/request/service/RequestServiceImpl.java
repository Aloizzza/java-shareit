package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    public List<RequestDto> findAllForOwner(PageRequest pageRequest, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));

        List<Request> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(pageRequest, userId);
        List<Item> items = itemRepository.findAllWithNonNullRequest();
        List<RequestDto> ownerRequests = new ArrayList<>();

        for (Request r : requests) {
            List<ItemDto> itemsFofRequest = new ArrayList<>();
            for (Item i : items) {
                if (r.getId() == i.getRequest().getId()) {
                    itemsFofRequest.add(ItemMapper.toItemDto(i, null, null, new ArrayList<>()));
                }
            }
            ownerRequests.add(RequestMapper.toRequestDto(r, itemsFofRequest));
        }

        return ownerRequests;
    }

    @Override
    public List<RequestDto> findAll(PageRequest pageRequest, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь c идентификатором " + userId + " не существует."));

        List<Request> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(pageRequest, userId);
        List<Item> items = itemRepository.findAllWithNonNullRequest();
        List<RequestDto> userRequests = new ArrayList<>();

        for (Request r : requests) {
            List<ItemDto> itemsFofRequest = new ArrayList<>();
            for (Item i : items) {
                if (r.getId() == i.getRequest().getId()) {
                    itemsFofRequest.add(ItemMapper.toItemDto(i, null, null, new ArrayList<>()));
                }
            }
            userRequests.add(RequestMapper.toRequestDto(r, itemsFofRequest));
        }

        return userRequests;
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
