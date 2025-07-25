package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film persist(Film film);

    Optional<Film> update(Film film);

    Collection<Film> getAll();

    Optional<Film> get(Long id);

    Collection<Film> getTopFilms(Long count);
}
