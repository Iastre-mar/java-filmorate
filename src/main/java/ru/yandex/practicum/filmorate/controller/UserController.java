package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userService.getUser(user.getId());
        return userService.updateUser(user)
                .get();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .get();
    }


    @GetMapping("{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id,
                                             @PathVariable Long otherId
    ) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id,
                             @PathVariable Long friendId
    ) {
        userService.removeFriend(id, friendId);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Long id
    ) {
        return filmService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Long id) {
        return userService.getEventsByUserId(id);
    }

}
