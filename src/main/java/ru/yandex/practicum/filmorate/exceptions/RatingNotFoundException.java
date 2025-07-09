package ru.yandex.practicum.filmorate.exceptions;

public class RatingNotFoundException extends FilmorateNotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
