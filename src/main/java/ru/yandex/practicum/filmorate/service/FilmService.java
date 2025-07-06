package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage") private final UserStorage userStorage;

    @LogMethodResult
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @LogMethodResult
    public Film add(Film film) {
        return filmStorage.persist(film);
    }

    @LogMethodResult
    public Optional<Film> update(Film film) {
        return filmStorage.update(film);
    }

    @LogMethodResult
    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId)
                               .get();
        User user = userStorage.get(userId)
                                       .get();

        film.getSetUserIdsLikedThis()
            .add(user.getId());
    }

    @LogMethodResult
    public void removeLikeFromFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId)
                               .get();
        User user = userStorage.get(userId)
                                       .get();
        film.getSetUserIdsLikedThis()
            .remove(user.getId());
    }

    public Collection<Film> getTopFilms(Long count) {
        return filmStorage.getTopFilms(count);
    }
}