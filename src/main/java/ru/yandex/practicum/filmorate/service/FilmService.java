package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @LogMethodResult
    public Collection<Film> getAll() {
        return inMemoryFilmStorage.getAll();
    }

    @LogMethodResult
    public Film add(Film film) {
        return inMemoryFilmStorage.persist(film);
    }

    @LogMethodResult
    public Optional<Film> update(Film film) {
        return inMemoryFilmStorage.update(film);
    }
}