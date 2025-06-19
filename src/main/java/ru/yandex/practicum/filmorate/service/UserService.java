package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @LogMethodResult
    public Collection<User> getAll() {
        return inMemoryUserStorage.getAll();
    }

    @LogMethodResult
    public User createUser(User user) {
        return inMemoryUserStorage.persist(user);
    }

    @LogMethodResult
    public Optional<User> update(User user) {
        return inMemoryUserStorage.update(user);
    }

}
