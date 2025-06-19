package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
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

    private long generateId() {
        return id++;
    }

    private Set<User> getSetOfFriends(User user) {
        return user.getFriendsIdSet()
                   .stream()
                   .map(users::get)
                   .collect(Collectors.toSet());
    }
}
