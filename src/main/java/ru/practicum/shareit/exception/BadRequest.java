package ru.practicum.shareit.exception;

public class BadRequest extends RuntimeException {

    public BadRequest(String message) {
        super(message);
    }
}