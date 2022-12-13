package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    @ToString.Exclude
    List<CommentDto> comments;
    private long requestId;

    @Data
    @AllArgsConstructor
    public static class BookingForItemDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                ", comments=" + comments +
                ", requestId=" + requestId +
                '}';
    }
}