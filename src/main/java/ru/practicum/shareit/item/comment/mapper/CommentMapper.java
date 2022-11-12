package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(
                0,
                commentDto.getText(),
                item,
                user,
                commentDto.getCreated()
        );
    }

    public static CommentDto toCommentDto(Comment comment, User user) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated()
        );
    }
}
