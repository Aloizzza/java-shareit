package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
}
