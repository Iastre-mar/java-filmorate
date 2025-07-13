package ru.yandex.practicum.filmorate.exceptions;

public class FilmorateNotFoundException extends RuntimeException {
    public FilmorateNotFoundException(String message) {
        super(message);
    }
}
