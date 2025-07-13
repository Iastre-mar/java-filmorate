package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends FilmorateNotFoundException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
