package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Rating> ratings = new HashMap<>();

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
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Film> getTopFilms(Long count) {
        return films.values()
                    .stream()
                    .sorted((f1, f2) -> Long.compare(
                            f2.getSetUserIdsLikedThis()
                              .size(), f1.getSetUserIdsLikedThis()
                                         .size()))
                    .limit(count)
                    .collect(Collectors.toList());
    }

    @Override
    public void loadLinkedDataForBatch(List<Film> films) {

    }

    @Override
    public void saveLinkedFilmData(Film film) {

    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        // Implement the logic to get common films
        return Collections.emptyList();
    }

    public Map<Long, Set<Long>> loadLikesForFilms(Set<Long> filmIds) {
        // Implement the logic to load likes for films
        return Collections.emptyMap();
    }

    public Map<Long, List<Genre>> loadGenresForFilms(Set<Long> filmIds) {
        // Implement the logic to load genres for films
        return Collections.emptyMap();
    }

    public Map<Long, Rating> loadRatingsByIds(Set<Long> ratingIds) {
        return ratingIds.stream()
                        .collect(Collectors.toMap(id -> id, ratings::get));
    }
}