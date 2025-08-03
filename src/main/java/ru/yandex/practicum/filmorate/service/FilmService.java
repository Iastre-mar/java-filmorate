package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    @Qualifier("filmDbStorage") private final FilmStorage filmStorage;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @LogMethodResult
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @LogMethodResult
    public Film add(Film film) {
        film.setGenres(genreService.getGenreOrThrow(film.getGenres()));
        film.setDirectors(
                directorService.getDirectorOrThrow(film.getDirectors()));
        ratingService.getRatingOrThrow(film.getRating()
                                           .getId());
        filmStorage.persist(film);

        Rating fullRating = ratingService.getRatingOrThrow(film.getRating()
                                                               .getId());
        film.setRating(fullRating);
        return film;
    }

    @LogMethodResult
    public Optional<Film> update(Film film) {
        getFilmByIdOrThrow(film.getId());
        film.setGenres(genreService.getGenreOrThrow(film.getGenres()));
        film.setDirectors(
                directorService.getDirectorOrThrow(film.getDirectors()));
        return filmStorage.update(film);
    }

    @LogMethodResult
    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId)
                               .get();

        film.getSetUserIdsLikedThis()
            .add(user.getId());

        filmStorage.saveLinkedFilmData(film);
    }

    @LogMethodResult
    public void removeLikeFromFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId)
                               .get();
        film.getSetUserIdsLikedThis()
            .remove(user.getId());

        filmStorage.saveLinkedFilmData(film);
    }

    @LogMethodResult
    public Collection<Film> getTopFilms(Long count) {
        return filmStorage.getTopFilms(count);
    }

    @LogMethodResult
    public Optional<Film> getFilmByIdOrThrow(Long id) {
        return filmStorage.get(id);
    }

    @LogMethodResult
    public Collection<Film> getDirectorFilms(Long directorId, String sortBy) {
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    @LogMethodResult
    public Collection<Film> getFilmsSearch(String query, List<String> by) {
        return filmStorage.getFilmsSearch(query, by);
    }
}