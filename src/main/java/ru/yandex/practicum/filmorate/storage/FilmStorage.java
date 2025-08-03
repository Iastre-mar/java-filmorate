package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film persist(Film film);

    Optional<Film> update(Film film);

    Collection<Film> getAll();

    Optional<Film> get(Long id);

    Collection<Film> getTopFilms(Long count, Long genreId, Integer year);

    Collection<Film> getRecommendationsForUser(Long userId);

    void loadLinkedDataForBatch(List<Film> films);

    Collection<Film> getDirectorFilms(Long directorId, String sortBy);

    Collection<Film> getFilmsSearch(String query, List<String> by);

    void saveLinkedFilmData(Film film);

    void delete(Long id);
}
