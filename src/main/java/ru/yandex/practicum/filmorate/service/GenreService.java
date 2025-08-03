package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @LogMethodResult
    public Genre getGenreOrThrow(Long id) {
        return genreStorage.get(id)
                .orElseThrow(() -> new GenreNotFoundException(
                        "Genre not found with id: " + id));
    }

    @LogMethodResult
    public Collection<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    @LogMethodResult
    protected Set<Genre> getGenreOrThrow(Set<Genre> genres) {
        Set<Genre> resGenres = getEmptyArrayIfNull(genres);
        return new LinkedHashSet<>(resGenres.stream()
                .map(genre -> getGenreOrThrow(genre.getId()))
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .toList());
    }

    private Set<Genre> getEmptyArrayIfNull(Set<Genre> genres) {
        return genres == null ? new HashSet<>() : genres;
    }

}
