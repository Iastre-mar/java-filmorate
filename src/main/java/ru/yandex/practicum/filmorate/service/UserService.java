package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @LogMethodResult
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @LogMethodResult
    public User createUser(User user) {
        return userRepository.persist(user);
    }

    @LogMethodResult
    public Optional<User> update(User user) {
        return userRepository.update(user);
    }

}
