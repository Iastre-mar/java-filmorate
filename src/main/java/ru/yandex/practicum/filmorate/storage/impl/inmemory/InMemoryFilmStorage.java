package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

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
    public Collection<Film> getTopFilms(Long count,
                                        Long genreId,
                                        Integer year
    ) {
        return films.values()
                    .stream()
                    .filter(f -> genreId == null ||
                                 f.getGenres()
                                  .stream()
                                  .anyMatch(g -> g.getId()
                                                  .equals(genreId)))
                    .filter(f -> year == null ||
                                 f.getReleaseDate()
                                  .getYear() == year)
                    .sorted((f1, f2) -> Integer.compare(
                            f2.getSetUserIdsLikedThis()
                              .size(), f1.getSetUserIdsLikedThis()
                                         .size()))
                    .limit(count != null ? count : 10)
                    .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getRecommendationsForUser(Long userId) {
        return List.of();
    }

    @Override
    public void loadLinkedDataForBatch(List<Film> films) {

    }

    private long generateId() {
        return id++;
    }
}


