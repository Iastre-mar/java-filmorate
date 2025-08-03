package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    @Qualifier("filmDbStorage") private final FilmStorage filmStorage;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;
    private final EventService eventService;
    private final DirectorService directorService;

    @LogMethodResult
    public Collection<Film> getAll() {

        return filmStorage.getAll();
    }

    @LogMethodResult
    public Film add(Film film) {
        validateFilm(film);
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
        validateFilm(film);
        getFilmByIdOrThrow(film.getId());
        film.setGenres(genreService.getGenreOrThrow(film.getGenres()));
        film.setDirectors(
                directorService.getDirectorOrThrow(film.getDirectors()));
        return filmStorage.update(film);
    }

    @LogMethodResult
    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        film.getSetUserIdsLikedThis()
            .add(user.getId());
        addLikeEvent(userId, filmId, Event.Operation.ADD);

        filmStorage.saveLinkedFilmData(film);
    }

    @LogMethodResult
    public void removeLikeFromFilm(Long filmId, Long userId) {
        Film film = getFilmByIdOrThrow(filmId).get();
        User user = userService.getUser(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        film.getSetUserIdsLikedThis().remove(user.getId());
        addLikeEvent(userId, filmId, Event.Operation.REMOVE);
        filmStorage.saveLinkedFilmData(film);
    }

    @LogMethodResult
    public Collection<Film> getRecommendations(Long userId) {
        userService.getUser(userId);
        return filmStorage.getRecommendationsForUser(userId);
    }

    @LogMethodResult
    public Collection<Film> getTopFilmsByGenreAndYear(Long count,
                                                      Long genreId,
                                                      Integer year
    ) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    @LogMethodResult
    public Collection<Film> getTopFilms(Long count,
                                        Long genreId,
                                        Integer year
    ) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    @LogMethodResult
    public Optional<Film> getFilmByIdOrThrow(Long id) {

        return filmStorage.get(id);
    }

    @LogMethodResult
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        userService.getUser(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        userService.getUser(friendId).orElseThrow(() -> new UserNotFoundException("Friend not found"));

        List<Film> commonFilms = (List<Film>) filmStorage.getCommonFilms(userId, friendId);
        loadLinkedDataForBatch(commonFilms);
        return commonFilms;
    }

    private void loadLinkedDataForBatch(List<Film> films) {
        Set<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());

        Map<Long, Set<Long>> likesMap = filmStorage.loadLikesForFilms(filmIds);
        Map<Long, List<Genre>> genresMap = filmStorage.loadGenresForFilms(filmIds);
        Map<Long, Rating> ratingsMap = filmStorage.loadRatingsByIds(
                films.stream().map(f -> f.getRating().getId()).collect(Collectors.toSet())
        );

        for (Film film : films) {
            film.setSetUserIdsLikedThis(likesMap.getOrDefault(film.getId(), new HashSet<>()));
            film.setGenres(genresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.setRating(ratingsMap.get(film.getRating().getId()));
        }
    }

    private void validateFilm(Film film) {
        film.setGenres(genreService.getGenreOrThrow(film.getGenres()));
        ratingService.getRatingOrThrow(film.getRating().getId());
    }
    @LogMethodResult
    public Collection<Film> getDirectorFilms(Long directorId, String sortBy) {
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    @LogMethodResult
    public Collection<Film> getFilmsSearch(String query, List<String> by) {
        return filmStorage.getFilmsSearch(query, by);
    }

    private void addLikeEvent(Long userId,
                              Long filmId,
                              Event.Operation operation
    ) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(filmId);
        event.setEventType(Event.EventType.LIKE);
        event.setOperation(operation);
        event.setTimestamp(System.currentTimeMillis());
        eventService.addEvent(event);
    }

}