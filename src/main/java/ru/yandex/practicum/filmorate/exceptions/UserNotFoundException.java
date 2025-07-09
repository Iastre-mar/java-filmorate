package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends FilmorateNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
