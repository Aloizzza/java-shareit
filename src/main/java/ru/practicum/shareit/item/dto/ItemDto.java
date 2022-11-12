package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    @ToString.Exclude
    List<CommentDto> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingForItemDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
    }
}