package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;

public interface FilmStorage {
    Film persist(Film film);

    Optional<Film> update(Film film);

    Collection<Film> getAll();

    Optional<Film> get(Long id);

    Collection<Film> getTopFilms(Long count);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    Map<Long, Set<Long>> loadLikesForFilms(Set<Long> filmIds);

    Map<Long, List<Genre>> loadGenresForFilms(Set<Long> filmIds);

    Map<Long, Rating> loadRatingsByIds(Set<Long> collect);
}
