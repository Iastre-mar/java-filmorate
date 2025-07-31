package ru.yandex.practicum.filmorate.exceptions;

public class DirectorNotFoundException extends FilmorateNotFoundException {
    public DirectorNotFoundException(String message) {
        super(message);
    }
}
