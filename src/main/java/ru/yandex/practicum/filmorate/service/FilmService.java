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
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;

    @LogMethodResult
    public Collection<Film> getAll() {

        return filmStorage.getAll();
    }

    @LogMethodResult
    public Film add(Film film) {
        film.setGenres(genreService.getGenreOrThrow(film.getGenres()));
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
        return filmStorage.update(film);
    }

    @LogMethodResult
    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId)
                               .get();

        film.getSetUserIdsLikedThis()
            .add(user.getId());

        update(film);
    }

    @LogMethodResult
    public void removeLikeFromFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId)
                               .get();
        film.getSetUserIdsLikedThis()
            .remove(user.getId());

        update(film);
    }

    @LogMethodResult
    public Collection<Film> getRecommendations(Long userId) {
        userService.getUser(userId);
        return filmStorage.getRecommendationsForUser(userId);
    }

    @LogMethodResult
    public Collection<Film> getTopFilmsByGenreAndYear(Long count, Long genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    @LogMethodResult
    public Collection<Film> getTopFilms(Long count, Long genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    @LogMethodResult
    public Optional<Film> getFilmByIdOrThrow(Long id) {

        return filmStorage.get(id);
    }

}