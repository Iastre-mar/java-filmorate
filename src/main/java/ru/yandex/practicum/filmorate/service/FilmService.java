package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Film;
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

    @LogMethodResult
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @LogMethodResult
    public Film add(Film film) {
        validateFilm(film);
        filmStorage.persist(film);
        return film;
    }

    @LogMethodResult
    public Optional<Film> update(Film film) {
        validateFilm(film);
        getFilmByIdOrThrow(film.getId());
        return filmStorage.update(film);
    }

    @LogMethodResult
    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId)
                               .orElseThrow(() -> new UserNotFoundException(
                                       "User not found"));

        film.getSetUserIdsLikedThis()
            .add(user.getId());
        filmStorage.update(film);
    }

    @LogMethodResult
    public void removeLikeFromFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId)
                               .orElseThrow(() -> new UserNotFoundException(
                                       "User not found"));

        film.getSetUserIdsLikedThis()
            .remove(user.getId());
        filmStorage.update(film);
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
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        userService.getUser(userId)
                   .orElseThrow(
                           () -> new UserNotFoundException("User not found"));
        userService.getUser(friendId)
                   .orElseThrow(() -> new UserNotFoundException(
                           "Friend not found"));

        List<Film> commonFilms = (List<Film>) filmStorage.getCommonFilms(
                userId, friendId);
        filmStorage.loadLinkedDataForBatch(commonFilms);
        return commonFilms;
    }

    private void validateFilm(Film film) {
        film.setGenres(genreService.getGenreOrThrow(film.getGenres()));
        ratingService.getRatingOrThrow(film.getRating()
                                           .getId());
    }
}