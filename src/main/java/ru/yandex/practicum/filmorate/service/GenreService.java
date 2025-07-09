package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    protected List<Genre> getGenreOrThrow(List<Genre> genres) {
        List<Genre> resGenres = getEmptyArrayIfNull(genres);
        resGenres = resGenres.stream()
                             .map(genre -> getGenreOrThrow(genre.getId()))
                             .toList();
        return resGenres;
    }

    private List<Genre> getEmptyArrayIfNull(List<Genre> genres) {
        return genres == null ? new ArrayList<>() : genres;
    }

}
