package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User persist(User user);

    Optional<User> update(User user);

    Collection<User> getAll();

    Optional<User> get(Long id);

    Collection<User> getFriends(Long id);
}
