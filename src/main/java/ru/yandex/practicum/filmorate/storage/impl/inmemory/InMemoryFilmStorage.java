package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;


    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> get(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film persist(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        return Optional.ofNullable(
                films.computeIfPresent(film.getId(), (k, v) -> film));
    }


    @Override
    public Collection<Film> getTopFilms(Long count) {
        return films.values()
                    .stream()
                    .sorted((film1, film2) -> film2.getSetUserIdsLikedThis()
                                                   .size() -
                                              film1.getSetUserIdsLikedThis()
                                                   .size())
                    .limit(Math.max(0, count))
                    .toList();
    }

    private long generateId() {
        return id++;
    }
}


