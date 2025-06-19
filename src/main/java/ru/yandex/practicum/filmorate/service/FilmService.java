package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

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
        User user = inMemoryUserStorage.get(userId)
                                       .get();

        film.getSetUserIdsLikedThis()
            .add(user.getId());
    }

    @LogMethodResult
    public void removeLikeFromFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId)
                               .get();
        User user = inMemoryUserStorage.get(userId)
                                       .get();
        film.getSetUserIdsLikedThis()
            .remove(user.getId());
    }

    public Collection<Film> getTopFilms(Long count) {
        return filmStorage.getTopFilms(count);
    }
}