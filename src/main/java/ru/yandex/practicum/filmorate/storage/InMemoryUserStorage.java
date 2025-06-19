package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User persist(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.ofNullable(
                users.computeIfPresent(user.getId(), (k, v) -> user));
    }

    @Override
    public User delete(User user) {
        return null;
    }

    private int generateId() {
        return id++;
    }
}
