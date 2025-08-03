package ru.yandex.practicum.filmorate.exceptions;

public class ReviewNotFoundException extends FilmorateNotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
