package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Set<User> getFriends(Long id) {
        return getSetOfFriends(users.get(id));
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
    public void delete(Long id) {
        users.remove(id); // Удаление пользователя из внутреннего хранилища
    }

    private long generateId() {
        return id++;
    }

    private Set<User> getSetOfFriends(User user) {
        return user.getFriendships()
                .stream()
                .map(f -> users.get(f.getFriendId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
